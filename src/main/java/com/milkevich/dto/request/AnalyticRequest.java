package com.milkevich.dto.request;

import com.milkevich.constant.SearchType;
import java.time.LocalDateTime;

public record AnalyticRequest(SearchType type, LocalDateTime createdBy, LocalDateTime updatedBy) {
}
