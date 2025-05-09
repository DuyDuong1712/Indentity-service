package com.devteria.identity_service.service;

import com.devteria.identity_service.dto.reponse.RoleReponse;
import com.devteria.identity_service.dto.request.RoleRequest;
import com.devteria.identity_service.entity.PermissionEntity;
import com.devteria.identity_service.entity.RoleEntity;
import com.devteria.identity_service.mapper.RoleMapper;
import com.devteria.identity_service.repository.PermissionRepository;
import com.devteria.identity_service.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    RoleMapper roleMapper;

    public RoleReponse createRole(RoleRequest request) {
        RoleEntity role = roleMapper.toRoleEntity(request);

        List<PermissionEntity> permissionEntities = permissionRepository.findAllById(request.getPermissions());
        role.setPermissions(new HashSet<>(permissionEntities));

        roleRepository.save(role);
        RoleReponse roleReponse = roleMapper.toRoleResponse(role);
        return roleReponse;
    }

    public List<RoleReponse> getAllRoles() {
        List<RoleEntity> roleEntities = roleRepository.findAll();
        return roleEntities.stream().map(roleMapper::toRoleResponse).collect(Collectors.toList());
    }

    public void deleteRole(String id) {
        roleRepository.deleteById(id);
    }

}
