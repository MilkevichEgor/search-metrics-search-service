package com.milkevich.repository;

import com.milkevich.entity.AddressData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface AddressDataElasticsearchRepository extends ElasticsearchRepository<AddressData, String> {
  Page<AddressData> getAddressDataByAddress(String address, Pageable pageable);

  Page<AddressData> findByPostcode(String postcode, Pageable pageable);

  void deleteById(String id);
}
