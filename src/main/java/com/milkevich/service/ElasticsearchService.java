package com.milkevich.service;

import com.milkevich.constant.KafkaTopic;
import com.milkevich.constant.SearchType;
import com.milkevich.dto.request.AnalyticRequest;
import com.milkevich.dto.request.SearchByAddressRequest;
import com.milkevich.dto.request.SearchByPostcodeRequest;
import com.milkevich.entity.AddressData;
import com.milkevich.repository.AddressDataElasticsearchRepository;
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
	Page<AddressData> addressData = addressDataElasticsearchRepository.findByPostcode(request.postcode(), pageable);

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
