package com.milkevich.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

public record AnalyticTestRequest(UUID id, String searchType, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
