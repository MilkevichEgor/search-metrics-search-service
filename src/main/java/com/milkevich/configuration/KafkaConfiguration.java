package com.milkevich.configuration;

import com.milkevich.dto.request.AnalyticRequest;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

@EnableKafka
@Configuration
class KafkaConfiguration {

  @Bean(name = "sendAnalyticProducerFactory")
  DefaultKafkaProducerFactory<String, AnalyticRequest> sendAnalyticProducerFactory(KafkaProperties properties) {
	Map<String, Object> producerProperties = properties.buildProducerProperties(null);
	producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
	producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
	return new DefaultKafkaProducerFactory<>(producerProperties);
  }

  @Bean(name = "sendAnalyticKafkaTemplate")
  KafkaTemplate<String, AnalyticRequest> sendAnalyticKafkaTemplate(DefaultKafkaProducerFactory<String, AnalyticRequest> producerFactory) {
	return new KafkaTemplate<>(producerFactory);
  }

  @Bean
  DefaultKafkaProducerFactory<String, String> stringProducerFactory(KafkaProperties properties) {
	Map<String, Object> producerProperties = properties.buildProducerProperties(null);
	producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
	producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
	return new DefaultKafkaProducerFactory<>(producerProperties);
  }

  @Bean(name = "stringKafkaTemplate")
  KafkaTemplate<String, String> stringKafkaTemplate(DefaultKafkaProducerFactory<String, String> stringProducerFactory) {
	return new KafkaTemplate<>(stringProducerFactory);
  }
}