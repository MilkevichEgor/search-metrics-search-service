package com.milkevich.controller;

import com.milkevich.dto.request.SearchByAddressRequest;
import com.milkevich.dto.request.SearchByPostcodeRequest;
import com.milkevich.entity.AddressData;
import com.milkevich.service.ElasticsearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	elasticsearchService.getAll(pageable).stream()
		.forEach(System.out::println);
  }

  @GetMapping("/address")
  public PagedModel<AddressData> searchAddressData(@RequestBody SearchByAddressRequest request,
											 @RequestParam(defaultValue = "0") int page,
											 @RequestParam(defaultValue = "100") int size) {
	Pageable pageable = PageRequest.of(page, size);
	Page<AddressData> addressData = elasticsearchService.searchByAddress(request, pageable);
	return new PagedModel<>(addressData);
  }

  @GetMapping(path = "/postcode")
  public PagedModel<AddressData> searchAddressDataByPostcode(@RequestBody SearchByPostcodeRequest request,
													   @RequestParam(defaultValue = "0") int page,
													   @RequestParam(defaultValue = "100") int size) {
	Pageable pageable = PageRequest.of(page, size);
	Page<AddressData> addressData = elasticsearchService.searchByPostcode(request, pageable);
	return new PagedModel<>(addressData);
  }

  @DeleteMapping(path = "/address")
  public void deleteByEntity(String id) {
	elasticsearchService.deleteById(id);
  }

  @DeleteMapping
  public void deleteAll() {
	elasticsearchService.deleteAll();
  }
}
