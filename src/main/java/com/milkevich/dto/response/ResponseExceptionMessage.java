package com.milkevich.dto.response;

import java.util.Date;

public record ResponseExceptionMessage(Date date, String message, String details) {
}
