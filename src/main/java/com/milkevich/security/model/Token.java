package com.milkevich.security.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface Token {
  UUID id();
  String subject();
  List<String> authorities();
  Instant createdAt();
  Instant expiresAt();
}

