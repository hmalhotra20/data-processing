package com.example.spark_example1;

import com.example.spark_example1.PurchaseOrder;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

import java.io.Serializable;
import java.util.*;

import static org.apache.spark.sql.functions.*;

public class PPEvaluatorJob5 implements Serializable {

    Map<String, Object> kafkaParams;

    public PPEvaluatorJob5()    {
        kafkaParams = new HashMap<>();
        kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, "grp1");
        kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    }

    public void evalStream()    {

        SparkConf sparkConf = new SparkConf().setMaster("local").setAppName("Petrol Pump Job2");
        Collection<String> topics = new HashSet<String>(Arrays.asList("test1"));

        JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(2));
        SQLContext sqlContext = new SQLContext(jssc.sparkContext());

        JavaInputDStream<ConsumerRecord<String, String>> stream = KafkaUtils.createDirectStream(
                jssc,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.Subscribe(topics, kafkaParams));

        StructType schema = new StructType(new StructField[]{
                new StructField("petrolPumpId", DataTypes.StringType, true, Metadata.empty()),
                new StructField("machineId", DataTypes.StringType, true, Metadata.empty()),
                new StructField("city", DataTypes.StringType, true, Metadata.empty()),
                new StructField("purchaseTime", DataTypes.StringType, true, Metadata.empty()),
                new StructField("fuelType", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("qnty", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("amt", DataTypes.IntegerType, true, Metadata.empty()),
                new StructField("pType", DataTypes.IntegerType, true, Metadata.empty())
        });

        JavaDStream<String> lineStream = stream.map(consumerRecord -> consumerRecord.value());
        JavaDStream<Row> orderStream = lineStream.map(new Function<String, Row>() {
            @Override
            public Row call(String s) throws Exception {
                List<String> fields = Arrays.asList(s.split("\\|"));
                Row row = null;
                if(fields != null && fields.size()>3)   {
                    row = RowFactory.create(fields.get(0), fields.get(1),fields.get(2),
                            fields.get(3),
                            Integer.valueOf(fields.get(4)), Integer.valueOf(fields.get(5)),
                            Integer.valueOf(fields.get(6)), Integer.valueOf(fields.get(7)) );
                }
                return row;
            }
        });

        orderStream.foreachRDD(new VoidFunction<JavaRDD<Row>>() {
            Dataset dataSet;
            PurchaseOrder o = new PurchaseOrder();
            @Override
            public void call(JavaRDD<Row> purchaseOrderJavaRDD) throws Exception {

                Dataset<Row> dataFrame = sqlContext.createDataFrame(purchaseOrderJavaRDD, schema);
                Dataset<Row> ds2 = dataFrame.groupBy(to_date(dataFrame.col("purchaseTime")).alias("pdate"),
                        hour(dataFrame.col("purchaseTime")).alias("hour"),
                        dataFrame.col("fuelType").alias("fuel"))
                        .agg(sum("qnty").alias("qty"), sum("amt").alias("amt"));
                //ds2.show();
                saveDayWiseDataMysql(ds2);
            }
        });

        try {
            jssc.start();
            jssc.awaitTermination();
        } catch (InterruptedException e) {}
        finally {
            //
        }
    }

    private void saveDayWiseDataMysql(Dataset<Row> dataSet)    {
        if(dataSet.count()>0)   {
            String mysql_url = "jdbc:mysql://root:mysql@localhost/sample";
            Properties connProperties = new Properties();
            //connProperties.setProperty("mode","append");
            dataSet.write().mode(SaveMode.Append).jdbc(mysql_url,"tbl2", connProperties);
        }
    }

    public static void main(String[] args) {
        PPEvaluatorJob5 job1 = new PPEvaluatorJob5();
        job1.evalStream();
    }

}
