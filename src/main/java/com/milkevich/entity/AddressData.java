package com.milkevich.entity;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "address")
public class AddressData {
  @Id
  private UUID id;
  private String address;
  private String postcode;
  private String latitude;
  private String longitude;
  private String buildingNumber;

  @Override
  public String toString() {
	return "AddressData{" +
		"id=" + id +
		", address='" + address + '\'' +
		", postcode='" + postcode + '\'' +
		", latitude='" + latitude + '\'' +
		", longitude='" + longitude + '\'' +
		", buildingNumber='" + buildingNumber + '\'' +
		'}';
  }
}
