package com.milkevich.repository;

import com.milkevich.entity.Role;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
  List<Role> findByUser_Id(UUID userId);
}
