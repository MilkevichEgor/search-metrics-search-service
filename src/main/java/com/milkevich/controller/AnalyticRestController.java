package com.milkevich.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.milkevich.constant.KafkaTopic;
import com.milkevich.dto.request.AnalyticRequest;
import com.milkevich.dto.request.AnalyticTestRequest;
import com.milkevich.producer.AnalyticProducerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "/api/send")
public class AnalyticRestController {

  private final ObjectMapper objectMapper;
  private final KafkaTemplate<String, String> stringKafkaTemplate;
  private final AnalyticProducerService analyticProducerService;

  public AnalyticRestController(ObjectMapper objectMapper,
								@Qualifier("stringKafkaTemplate") KafkaTemplate<String, String> stringKafkaTemplate,
								AnalyticProducerService analyticProducerService) {
	this.objectMapper = objectMapper;
	this.stringKafkaTemplate = stringKafkaTemplate;
	this.analyticProducerService = analyticProducerService;
  }

  @PostMapping
  public void sendAnalytic(@RequestBody AnalyticRequest request) {
	analyticProducerService.sendAnalytic(request);
  }

  @PostMapping(path = "/clickhouse")
  public void sendInClickhouse(@RequestBody AnalyticTestRequest test) throws JsonProcessingException {
	String request = objectMapper.writeValueAsString(test);
	stringKafkaTemplate.send(KafkaTopic.SEND_IN_CLICKHOUSE_TOPIC, request);
  }
}
