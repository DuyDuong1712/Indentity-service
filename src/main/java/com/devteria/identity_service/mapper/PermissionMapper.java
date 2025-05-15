package com.devteria.identity_service.mapper;

import org.mapstruct.Mapper;

import com.devteria.identity_service.dto.reponse.PermissionReponse;
import com.devteria.identity_service.dto.request.PermissionRequest;
import com.devteria.identity_service.entity.PermissionEntity;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    PermissionEntity toPermission(PermissionRequest request);

    PermissionReponse toPermissionResponse(PermissionEntity permissionEntity);
    //    void updatePermission(@MappingTarget Permission permission, UserUpdateRequest request);

}
