package com.devteria.identity_service.service;

import com.devteria.identity_service.dto.reponse.PermissionReponse;
import com.devteria.identity_service.dto.request.PermissionRequest;
import com.devteria.identity_service.entity.PermissionEntity;
import com.devteria.identity_service.mapper.PermissionMapper;
import com.devteria.identity_service.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;


    public PermissionReponse createPermission(PermissionRequest request){
        PermissionEntity permissionEntity = permissionMapper.toPermission(request);
        permissionRepository.save(permissionEntity);
        return permissionMapper.toPermissionResponse(permissionEntity);
    }

    public List<PermissionReponse> getAllPermission(){
        List<PermissionEntity> permissionEntities = permissionRepository.findAll();
        return permissionEntities.stream().map(permissionMapper::toPermissionResponse).collect(Collectors.toList());
    }

    public void deletePermission(String code){
        permissionRepository.deleteById(code);
    }
}
