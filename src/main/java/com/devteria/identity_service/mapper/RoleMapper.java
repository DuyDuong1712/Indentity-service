package com.devteria.identity_service.mapper;

import com.devteria.identity_service.dto.reponse.PermissionReponse;
import com.devteria.identity_service.dto.reponse.RoleReponse;
import com.devteria.identity_service.dto.request.PermissionRequest;
import com.devteria.identity_service.dto.request.RoleRequest;
import com.devteria.identity_service.entity.PermissionEntity;
import com.devteria.identity_service.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    RoleEntity toRoleEntity(RoleRequest request);

    RoleReponse toRoleResponse(RoleEntity roleEntity);
//    void updatePermission(@MappingTarget Permission permission, UserUpdateRequest request);


}
