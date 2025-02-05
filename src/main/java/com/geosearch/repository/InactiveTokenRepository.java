package com.geosearch.repository;

import com.geosearch.entity.InactiveToken;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InactiveTokenRepository extends JpaRepository<InactiveToken, UUID> {

  boolean existsById(UUID id);
}
