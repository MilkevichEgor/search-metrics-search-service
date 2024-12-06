package com.fusiontech.controller;

import com.fusiontech.entity.AddressData;
import com.fusiontech.service.ElasticsearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "api/elastic")
public class ElasticRestController {

  private final ElasticsearchService elasticsearchService;

  @GetMapping("/all")
  public void getAllAddressData(
	  @RequestParam(defaultValue = "0") int page,
	  @RequestParam(defaultValue = "10") int size
  ) {
	PageRequest pageable = PageRequest.of(page, size);
	elasticsearchService.getAll(pageable).stream().forEach(System.out::println);
  }

  @GetMapping("/search")
  public Page<AddressData> searchAddressData(@RequestParam String query,
											 @RequestParam(defaultValue = "0") int page,
											 @RequestParam(defaultValue = "100") int size
  ) {
	Pageable pageable = PageRequest.of(page, size);
	return elasticsearchService.search(query, pageable);
  }

  @DeleteMapping(path = "/address")
  public void deleteByEntity(AddressData addressData) {
	elasticsearchService.deleteByEntity(addressData);
  }

  @DeleteMapping
  public void deleteAll() {
	elasticsearchService.deleteAll();
  }
}
