package com.geosearch.service;

import com.geosearch.constant.KafkaTopic;
import com.geosearch.constant.SearchType;
import com.geosearch.dto.request.SearchByAddressRequest;
import com.geosearch.dto.request.SearchByPostcodeRequest;
import com.geosearch.dto.request.AnalyticRequest;
import com.geosearch.entity.AddressData;
import com.geosearch.repository.AddressDataElasticsearchRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticsearchService {

  private final KafkaTemplate<String, AnalyticRequest> kafkaTemplate;

  private final AddressDataElasticsearchRepository addressDataElasticsearchRepository;


  public Page<AddressData> searchByAddress(SearchByAddressRequest request, Pageable pageable) {
	Page<AddressData> addressData = addressDataElasticsearchRepository.getAddressDataByAddress(request.address(), pageable);

	AnalyticRequest analyticRequest = new AnalyticRequest(SearchType.ADDRESS, LocalDateTime.now(), LocalDateTime.now());
	kafkaTemplate.send(KafkaTopic.SEND_ANALYTIC_TOPIC, analyticRequest);

	return addressData;
  }

  public Page<AddressData> searchByPostcode(SearchByPostcodeRequest request, Pageable pageable) {
	Page<AddressData> addressData = addressDataElasticsearchRepository.findByPostcode(request.postcode() , pageable);

	AnalyticRequest analyticRequest = new AnalyticRequest(SearchType.POSTCODE, LocalDateTime.now(), LocalDateTime.now());
	kafkaTemplate.send(KafkaTopic.SEND_ANALYTIC_TOPIC, analyticRequest);

	return addressData;
  }

  public Page<AddressData> getAll(Pageable pageable) {
	return addressDataElasticsearchRepository.findAll(pageable);
  }

  @Transactional
  public void deleteById(String id) {
	addressDataElasticsearchRepository.deleteById(id);
  }

  @Transactional
  public void deleteAll() {
	addressDataElasticsearchRepository.deleteAll();
	log.info("Данные в elasticsearch удалены");
  }
}
