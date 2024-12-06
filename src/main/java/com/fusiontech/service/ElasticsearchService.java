package com.fusiontech.service;

import com.fusiontech.entity.AddressData;
import com.fusiontech.repository.AddressDataElasticsearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticsearchService {

  private final AddressDataElasticsearchRepository addressDataElasticsearchRepository;

  public Page<AddressData> search(String query, Pageable pageable) {
	return addressDataElasticsearchRepository.getAddressDataByAddress(query, pageable);
  }

  public Page<AddressData> getAll(Pageable pageable) {
	return addressDataElasticsearchRepository.findAll(pageable);
  }

  @Transactional
  public void deleteByEntity(AddressData addressData) {
	addressDataElasticsearchRepository.delete(addressData);
  }

  @Transactional
  public void deleteAll() {
	addressDataElasticsearchRepository.deleteAll();
	log.info("Данные в elasticsearch удалены");
  }
}
