package com.fusiontech.service;

import com.fusiontech.entity.AddressData;
import com.fusiontech.repository.AddressDataElasticsearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressDataService {

  private final AddressDataElasticsearchRepository addressDataElasticsearchRepository;

  public Page<AddressData> search(String query, Pageable pageable) {
	return addressDataElasticsearchRepository.getAddressDataByAddress(query, pageable);
  }
}
