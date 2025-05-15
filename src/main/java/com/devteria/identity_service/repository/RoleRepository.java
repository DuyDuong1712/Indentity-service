package com.devteria.identity_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devteria.identity_service.entity.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity, String> {}
