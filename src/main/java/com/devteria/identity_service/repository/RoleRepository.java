package com.devteria.identity_service.repository;

import com.devteria.identity_service.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, String> {
}
