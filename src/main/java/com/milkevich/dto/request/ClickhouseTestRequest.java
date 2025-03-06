package com.milkevich.dto.request;

import java.util.UUID;

public record ClickhouseTestRequest(UUID id, String searchType, String createdAt, String updatedAt) {
}
