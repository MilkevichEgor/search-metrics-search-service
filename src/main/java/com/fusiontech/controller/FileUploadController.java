package com.fusiontech.controller;

import com.fusiontech.repository.AddressDataElasticsearchRepository;
import com.fusiontech.service.CsvParserService;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/upload")
public class FileUploadController {

  private final CsvParserService csvParserService;
  private final AddressDataElasticsearchRepository elasticsearchRepository;

  @PostMapping("/csv")
  public void fileUpload(@RequestBody List<MultipartFile> files) {
	csvParserService.uploadFiles(files);
  }

  @GetMapping("/all")
  public void getAllAddressData(
	  @RequestParam(defaultValue = "0") int page,
	  @RequestParam(defaultValue = "10") int size
  ) {
	PageRequest pageable = PageRequest.of(page, size);
	elasticsearchRepository.findAll(pageable).stream().forEach(System.out::println);
  }
}
