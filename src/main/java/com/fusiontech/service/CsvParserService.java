package com.fusiontech.service;

import com.fusiontech.entity.AddressData;
import com.fusiontech.repository.AddressDataElasticsearchRepository;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Log4j2
public class CsvParserService {

  private final ExecutorService executor = Executors.newFixedThreadPool(4);
  private final AddressDataElasticsearchRepository addressDataElasticsearchRepository;
  private final Path uploadDir = Paths.get("./src/main/resources/uploads");

  public void uploadFiles(List<MultipartFile> multipartFiles) {
	multipartFiles.forEach(file -> {
	  String filePath = saveFile(file);
	  processFileAsync(filePath);
	});
	log.info("All files have been queued for processing.");
  }

  public String saveFile(MultipartFile file) {
	try {
	  String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
	  Path filePath = uploadDir.resolve(filename);
	  Files.copy(file.getInputStream(), filePath);
	  log.info("File saved: " + filePath);
	  return filePath.toString();

	} catch (IOException e) {
	  throw new RuntimeException("Failed to save file: " + e.getMessage(), e);
	}
  }

  public List<AddressData> parseCsvFile(File file) {

	CSVFormat format = CSVFormat.Builder.create(CSVFormat.DEFAULT)
		.setHeader()
		.setSkipHeaderRecord(true)
		.build();

	try (FileReader fileReader = new FileReader(file);

		 CSVParser csvParser = new CSVParser(fileReader, format)) {

	  Iterable<CSVRecord> csvRecords = csvParser.getRecords();
	  List<AddressData> addressList = new ArrayList<>();

	  for (CSVRecord record : csvRecords) {
		AddressData addressData = new AddressData(
			UUID.randomUUID(),
			record.get(0),
			record.get(1),
			record.get(2),
			record.get(3),
			record.get(4)
		);
		addressList.add(addressData);
	  }
	  return addressList;
	} catch (IOException e) {
	  log.error("Error parsing file: " + file.getName(), e);
	  return List.of();
	}
  }

  @Async
  public void processFileAsync(String filePath) {
	try {
	  log.info("Постановка в очередь файла: {}", filePath);
	  executor.submit(() -> {
		try {
		  Thread.sleep(5000);

		  File file = new File(filePath);

		  log.info("Обработка файла {} стартовала", filePath);
		  parseAndSave(file);
		  log.info("Обработка файла {} завершена", filePath);

		  if (file.delete()) {
			log.info("File {} deleted successfully", file.getName());
		  }
		} catch (Exception e) {
		  log.error("Ошибка при обработке файла: " + filePath, e);
		}
	  });
	} catch (Exception e) {
	  log.error("Error processing file: " + filePath, e);
	}
  }

  public void saveAddressesAsync(List<AddressData> addressList) {
	addressDataElasticsearchRepository.saveAll(addressList);
  }

  public void parseAndSave(File csvFile) {

	int batchSize = 100000;
	List<AddressData> addressList = parseCsvFile(csvFile);
	List<AddressData> batch = new ArrayList<>();

	for (int i = 0; i < addressList.size(); i++) {
	  batch.add(addressList.get(i));
	  if (batch.size() == batchSize || i == addressList.size() - 1) {
		saveAddressesAsync(new ArrayList<>(batch));
		batch.clear();
	  }
	}
  }
}
