package main.scala

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.functions._
import org.apache.spark.SparkContext._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Duration, Seconds, StreamingContext}
import org.apache.spark.SparkContext
import org.apache.spark.sql._
import org.apache.spark.sql.types._
import org.apache.spark.SparkConf
import com.memsql.spark.connector._
import com.memsql.spark.connector.util._
import com.memsql.spark.connector.util.JDBCImplicits._


object Main {

  def main(args: Array[String]) {

    /*val conf = new SparkConf()
    conf.setAppName("Datasets Test")
    conf.setMaster("local[2]")
    val sparkContext = new SparkContext(conf)
    val rdd = sparkContext.parallelize(((1 to 20).map(x => ("key", x))), 4)
    rdd.reduceByKey(_ + _)
    rdd.collect()*/
    //sparkTry()
    kafkaTrial()
  }

  def kafkaTrial(): Unit =  {
    val sparkConf = new SparkConf().setAppName("k").setMaster("local[2]")
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    val lines = KafkaUtils.createStream(ssc,"localhost:2181","grp1",Map("test1"->1), StorageLevel.MEMORY_ONLY_2)
    lines.print()


    ssc.start()
    ssc.awaitTermination()
  }

  def sparkTry(): Unit = {
    val session = SparkSession.builder.appName("flipkart").master("local").getOrCreate
    val schema = new StructType().add("uniq_id", "string")
      .add("crawl_timestamp", "string").add("product_url", "string")
      .add("product_name", "string").add("product_category_tree", "string")
      .add("pid", "string").add("retail_price", "string")
      .add("discounted_price", "string").add("image", "string")
      .add("is_FK_Advantage_product", "string").add("description", "string")
      .add("product_rating", "string").add("overall_rating", "string")
      .add("brand", "string").add("product_specifications", "string")

    val df = session.read.format("com.databricks.spark.csv")
      .option("delimiter", ",").option("header", "true")
      .option("inferSchema", "true").option("escape", "\"")
      .load("/Users/hemantm/work/tmp/flipkart_com-ecommerce_sample.csv")

    //df.show(20);
    //df.printSchema()1
    val df1 = df.groupBy("brand").agg(count("*").alias("cnt"), avg("product_rating").alias("sum_rting"))
    df1.show(20)
    df1.orderBy(desc("cnt")).show(20)
  }

}
