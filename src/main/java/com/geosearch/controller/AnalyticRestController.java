package com.geosearch.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geosearch.constant.KafkaTopic;
import com.geosearch.dto.request.AnalyticRequest;
import com.geosearch.dto.request.AnalyticTestRequest;
import com.geosearch.producer.AnalyticProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/send")
public class AnalyticRestController {

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  @Qualifier("stringKafkaTemplate")
  private final KafkaTemplate<String, String> stringKafkaTemplate;

  private final AnalyticProducerService analyticProducerService;

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
