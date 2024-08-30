package com.example.spark_example1;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
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

import static org.apache.spark.sql.functions.sum;
import static org.apache.spark.sql.functions.to_date;

public class PPEvaluatorJob4 implements Serializable {

    Map<String, Object> kafkaParams;

    public PPEvaluatorJob4()    {
        kafkaParams = new HashMap<>();
        kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, "grp2");
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

        StructType schema = buildSchema();

        JavaDStream<String> lineStream = stream.map(consumerRecord -> consumerRecord.value());

        JavaDStream<Row> rowStream = lineStream.map(s -> {
            List<String> fields = Arrays.asList(s.split("\\|"));
            Row row = RowFactory.create(fields.get(0), fields.get(1), fields.get(2),
                    fields.get(3),
                    Integer.valueOf(fields.get(4)), Integer.valueOf(fields.get(5)),
                    Integer.valueOf(fields.get(6)), Integer.valueOf(fields.get(7)));
            return row;
        });

        rowStream.foreachRDD(purchaseRDDRow -> {
            Dataset<Row> dataFrame = sqlContext.createDataFrame(purchaseRDDRow, schema);
            Dataset<Row> df = dataFrame.groupBy(to_date(dataFrame.col("purchaseTime")).alias("pdate"),
                    dataFrame.col("city")).agg(sum("amt").alias("total_amt"));
            df.show();
            String mysql_url = "jdbc:mysql://root:mysql@localhost/sample";
            Properties connProperties = new Properties();
            connProperties.setProperty("mode","append");
            df.write().mode(SaveMode.Append).jdbc(mysql_url,"tbl3", connProperties);
        });

        try {
            jssc.start();
            jssc.awaitTermination();
        } catch (InterruptedException e) {}
        finally {
            //
        }
    }

    private StructType buildSchema()    {
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
        return schema;
    }

    public static void main(String[] args) {
        PPEvaluatorJob4 job2 = new PPEvaluatorJob4();
        job2.evalStream();
    }

}
