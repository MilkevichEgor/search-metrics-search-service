package com.geosearch.producer;

import com.geosearch.constant.KafkaTopic;
import com.geosearch.dto.request.AnalyticRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticProducerService {

  private final KafkaTemplate<String, AnalyticRequest> sendAnalyticKafkaTemplate;

  public void sendAnalytic(AnalyticRequest request) {
    sendAnalyticKafkaTemplate.send(KafkaTopic.SEND_ANALYTIC_TOPIC, request);
    log.info("Сообщение {}, отправлено", request.type());
  }

}
