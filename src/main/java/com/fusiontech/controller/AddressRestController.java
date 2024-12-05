package com.fusiontech.controller;

import com.fusiontech.entity.AddressData;
import com.fusiontech.service.AddressDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/address")
public class AddressRestController {

  private final AddressDataService addressDataService;

  @GetMapping("/search")
  public Page<AddressData> searchAddressData(@RequestParam String query,
											 @RequestParam(defaultValue = "0") int page,
											 @RequestParam(defaultValue = "100") int size
  ) {
	Pageable pageable = PageRequest.of(page, size);
	return addressDataService.search(query, pageable);
  }
}
