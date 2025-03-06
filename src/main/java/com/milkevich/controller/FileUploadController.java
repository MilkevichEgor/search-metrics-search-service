package com.milkevich.controller;

import com.milkevich.service.CsvParserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/upload")
public class FileUploadController {

  private final CsvParserService csvParserService;

  @PostMapping("/csv")
  public void fileUpload(@RequestBody List<MultipartFile> files) {
	csvParserService.uploadFiles(files);
  }
}
