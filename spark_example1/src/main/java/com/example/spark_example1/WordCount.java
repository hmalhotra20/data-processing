package com.example.spark_example1;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;

import scala.Tuple2;

public class WordCount implements Serializable {
	
	public static void wordCountJava7(String filename) {

		SparkConf conf = new SparkConf().setAppName("wordCount").setMaster("local[2]");

		// Create a Java Spark Context
		JavaSparkContext javaSparkContext = new JavaSparkContext(conf);
		SparkConf conf1 = new SparkConf().setMaster("local").setAppName("Work Count App");

		// Load our input data.
		JavaRDD<String> input = javaSparkContext.textFile(filename);

		// Split up into words.
		JavaRDD<String> words = input.flatMap(
				new FlatMapFunction<String, String>() {
					public Iterator<String> call(String x) {
						return Arrays.asList(x.split(" ")).iterator();
					}});

		// Transform into word and count.
		JavaPairRDD<String, Integer> counts = words.mapToPair(
				new PairFunction<String, String, Integer>(){
					public Tuple2<String, Integer> call(String x){
						return new Tuple2(x, 1);
					}});

		JavaPairRDD<String, Integer> reducedCounts = counts.reduceByKey(new Function2<Integer, Integer, Integer>() {
			public Integer call(Integer x, Integer y) {
				return x + y;
			}
		});

		//reducedCounts.saveAsTextFile("output.txt");
		List<Tuple2<String, Integer>> data = reducedCounts.collect();
		for(Tuple2 data1 : data)	{
			System.out.println("model: " + data1._1() + ", value: " + data1._2());
		}
	}

	public static void wordCountJava8(String filename) {

		SparkConf conf1 = new SparkConf().setMaster("local").setAppName("Work Count App");
		JavaSparkContext sc = new JavaSparkContext(conf1);
		JavaRDD<String> input = sc.textFile(filename, 2);

		//JavaPairRDD<String, Integer> pairs = input.mapToPair(s -> new Tuple2(s, 1));
		//JavaPairRDD<String, Integer> counts = pairs.reduceByKey((a, b) -> a + b);

		JavaRDD<String> words = input.flatMap(s -> Arrays.asList(s.split(" ")).iterator());
		JavaPairRDD<String, Integer> counts = words.mapToPair(t -> new Tuple2(t, 1)).reduceByKey((x, y) -> (int) x + (int) y);

        counts.foreach(data -> {
            System.out.println("model: " + data._1() + ", value: " + data._2());
        });
	}
	
	public static void flipkartSample() {

		SparkSession session = SparkSession.builder().appName("flipkart").master("local").getOrCreate();
		StructType schema = new StructType()
			    .add("uniq_id", "string")
			    .add("crawl_timestamp", "string")
			    .add("product_url", "string")
			    .add("product_name", "string")
			    .add("product_category_tree", "string")
			    .add("pid", "string")
			    .add("retail_price", "string")
				.add("discounted_price", "string")
				.add("image","string")
				.add("is_FK_Advantage_product","string")
				.add("description","string")
				.add("product_rating","string")
				.add("overall_rating","string")
				.add("brand","string")
				.add("product_specifications","string");
		
		Dataset<Row> df = session.read()
				.format("com.databricks.spark.csv")
				.option("delimiter", ",")
				.option("header", "true") // Use first line of all files as header
			    .option("inferSchema", "true") // Automatically infer data types
			    .option("escape", "\"")
			    .load("/Users/hemantm/work/tmp/flipkart_com-ecommerce_sample.csv");
		
		df.printSchema();
		Object collect = df.groupBy("product_name").count().cache().collect();
		df.show(20);
		
	}

	public static void main(String[] args) {
		//wordCountJava7("/tmp/input.txt");
		wordCountJava8("/Users/hemantm/work/tmp/input.txt");
		//flipkartSample();
	}
}

