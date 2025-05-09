package com.devteria.identity_service.mapper;

import com.devteria.identity_service.dto.reponse.UserResponse;
import com.devteria.identity_service.dto.request.UserCreationRequest;
import com.devteria.identity_service.dto.request.UserUpdateRequest;
import com.devteria.identity_service.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity toUser(UserCreationRequest request);

    UserResponse toUserResponse(UserEntity userEntity);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget UserEntity userEntity, UserUpdateRequest request);


}
