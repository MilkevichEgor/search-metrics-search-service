package com.geosearch.dto.request;

import com.geosearch.constant.SearchType;
import java.time.LocalDateTime;

public record AnalyticRequest(SearchType type, LocalDateTime createdBy, LocalDateTime updatedBy) {
}
