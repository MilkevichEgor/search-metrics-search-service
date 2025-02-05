package com.geosearch.service;

import com.geosearch.entity.AddressData;
import com.geosearch.repository.AddressDataElasticsearchRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressDataService {

  private final AddressDataElasticsearchRepository addressDataElasticsearchRepository;

  @Transactional
  public void saveAddressesAsync(List<AddressData> addressList) {
	addressDataElasticsearchRepository.saveAll(addressList);
  }
}
