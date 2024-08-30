package com.example.demo.bigdata.producer;

import lombok.Data;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties
@PropertySource("classpath:cities.properties")
@EnableScheduling
@Data
public class Config {
	
	String email;
	
	@Value("${noPumps:10}")
	Integer noPumps;
	
	@Value("${pumpCapacity:10}")
	Integer pumpCapacity;
	
	@Value("${cityNames}")
	String cityNames;
	
	@Value("${allowedHitsPerTick:10}")
	Long allowedHitsPerTick;
	
	@Value("${tickTime:1000}")
	Long tickTime;

	@Value(("${incrementTimeEveryTick:1}"))
	Integer incrementTimeEveryTick;

	@Value(("${writeToQueueOrFile:true}"))
	boolean writeToQueueOrFile;

	@Value("${kafkaServersList}")
	String kafkaServersList;

	@Value("${filePath:/tmp/data.txt}")
	String filePath;

	//@Value("#{new java.text.SimpleDateFormat(‘${initialDateFormat}’).parse(‘${initialDate}’)}")
    @Value("${initialDate}")
	String initialDate;

    @Value("${withInitialDateOrCurrent}")
    Boolean withInitialDateOrCurrent;

	@Bean
	public KafkaTemplate<Integer, String> kafkaTemplate() {
		Map<String, Object> senderProps = senderProps();
		ProducerFactory<Integer, String> pf =
				new DefaultKafkaProducerFactory<Integer, String>(senderProps);
		KafkaTemplate<Integer, String> template = new KafkaTemplate<>(pf);
		return template;
	}

	private Map<String, Object> senderProps() {
		//System.out.println("Kafka: "+  kafkaServersList);
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServersList);
		props.put(ProducerConfig.RETRIES_CONFIG, 0);
		props.put("acks", "all");
		props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
		props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		return props;
	}
}
