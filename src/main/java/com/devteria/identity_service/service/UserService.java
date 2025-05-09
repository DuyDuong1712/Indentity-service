package com.devteria.identity_service.service;

import com.devteria.identity_service.dto.reponse.UserResponse;
import com.devteria.identity_service.dto.request.UserCreationRequest;
import com.devteria.identity_service.dto.request.UserUpdateRequest;
import com.devteria.identity_service.entity.RoleEntity;
import com.devteria.identity_service.entity.UserEntity;
import com.devteria.identity_service.enums.Role;
import com.devteria.identity_service.exception.AppException;
import com.devteria.identity_service.exception.ErrorCode;
import com.devteria.identity_service.mapper.UserMapper;
import com.devteria.identity_service.repository.RoleRepository;
import com.devteria.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTS);
        }

        UserEntity userEntity = userMapper.toUser(request);
//        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));

        Set<RoleEntity> roles = new HashSet<>();
        roles.add(roleRepository.findById(Role.USER.name()).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_EXISTS)));

        userEntity.setRoles(roles);

        UserEntity user = userRepository.save(userEntity);
        return userMapper.toUserResponse(user);
    }


    public UserResponse getMyInfo() {
        // Lấy thông tin người dùng hiện tại từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Lấy username

        UserEntity userEntity =  userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));
        return userMapper.toUserResponse(userEntity);
    }


    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
    }

    public UserResponse getUserById(String id) {
        UserEntity userEntity =  userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));

        return userMapper.toUserResponse(userEntity);
    }

    public UserResponse updateUser(String id, UserUpdateRequest request) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));

        userMapper.updateUser(userEntity, request);
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));

        List<RoleEntity> roles = roleRepository.findAllById(request.getRoles());
        userEntity.setRoles(new HashSet<>(roles));

        return userMapper.toUserResponse(userRepository.save(userEntity));
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
}
