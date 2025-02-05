package com.geosearch.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClickhouseTestRequest(UUID id, String searchType, String createdAt, String updatedAt) {
}
