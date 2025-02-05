package com.geosearch.configuration;

import com.geosearch.constant.KafkaTopic;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaAdminTopic {

  @Bean
  public NewTopic getLastSalaryTopic() {
	return TopicBuilder.name(KafkaTopic.SEND_ANALYTIC_TOPIC)
		.partitions(3)
		.replicas(1)
		.build();
  }
}
