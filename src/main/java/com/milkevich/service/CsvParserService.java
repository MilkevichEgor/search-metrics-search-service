package com.milkevich.service;

import com.milkevich.entity.AddressData;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Log4j2
public class CsvParserService {

  private final ExecutorService executor = Executors.newFixedThreadPool(4);
  private final AddressDataService addressDataService;
  @Value("${upload.file.path}")
  private Path uploadDir;
//  private final Path uploadDir = Paths.get("./src/main/resources/uploads");

  public void uploadFiles(List<MultipartFile> multipartFiles) {
	multipartFiles.forEach(file -> {
	  String filePath = saveFile(file);
	  processFileAsync(filePath);
	});
	log.info("Все файлы поставлены в очередь на обработку.");
  }

  private String saveFile(MultipartFile file) {
	try {
	  String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
	  Path filePath = uploadDir.resolve(filename);
	  Files.copy(file.getInputStream(), filePath);
	  log.info("Файл сохранён: {}", filePath);
	  return filePath.toString();
	} catch (IOException e) {
	  throw new IllegalArgumentException("Ошибка сохранения файла: " + e.getMessage(), e);
	}
  }

  private List<AddressData> parseCsvFile(File file) {
	CSVFormat format = CSVFormat.Builder.create(CSVFormat.DEFAULT)
		.setHeader()
		.setSkipHeaderRecord(true)
		.build();

	try (FileReader fileReader = new FileReader(file);
		 CSVParser csvParser = new CSVParser(fileReader, format)) {

	  Iterable<CSVRecord> csvRecords = csvParser.getRecords();
	  List<AddressData> addressList = new ArrayList<>();

	  for (CSVRecord csvData : csvRecords) {
		AddressData addressData = new AddressData(
			UUID.randomUUID(),
			csvData.get(0),
			csvData.get(1),
			csvData.get(2),
			csvData.get(3),
			csvData.get(4)
		);
		addressList.add(addressData);
	  }
	  return addressList;
	} catch (IOException e) {
	  log.error("Ошибка парсинга файла: {}", file.getName(), e);
	  return List.of();
	}
  }

  private void processFileAsync(String filePath) {
	try {
	  log.info("Файл поставлен в очередь на обработку: {}", filePath);
	  executor.submit(() -> {
		try {
		  Thread.sleep(5000);

		  File file = new File(filePath);
		  log.info("Обработка файла {} стартовала", filePath);
		  parseAndSave(file);
		  log.info("Обработка файла {} завершена", filePath);

		  // Если файл обработан успешно, его можно удалить
		  if (file.delete()) {
			log.info("Файл {} успешно удалён", file.getName());
		  } else {
			log.warn("Ошибка удаления файла {}", file.getName());
		  }
		} catch (InterruptedException e) {
		  log.error("Ошибка обработки файла: {}", filePath, e);
		  Thread.currentThread().interrupt();
		} catch (Exception e) {
		  log.error("Ошибка при обработке файла: {}", filePath, e);
		}
	  });
	} catch (Exception e) {
	  log.error("Ошибка при ассинхронной обработке файла: {}", filePath, e);
	}
  }

  private void parseAndSave(File csvFile) {
	int batchSize = 100000;
	List<AddressData> addressList = parseCsvFile(csvFile);
	List<AddressData> batch = new ArrayList<>();

	for (int i = 0; i < addressList.size(); i++) {
	  batch.add(addressList.get(i));
	  if (batch.size() == batchSize || i == addressList.size() - 1) {
		addressDataService.saveAddressesAsync(new ArrayList<>(batch));
		batch.clear();
	  }
	}
  }
}
