package com.devteria.identity_service.configuration;

import com.devteria.identity_service.entity.UserEntity;
import com.devteria.identity_service.enums.Role;
import com.devteria.identity_service.repository.PermissionRepository;
import com.devteria.identity_service.repository.RoleRepository;
import com.devteria.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.devteria.identity_service.entity.RoleEntity;

import java.util.HashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    //Khởi tạo user Admin
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            // Nếu ứng dụng lần đầu khởi động, chưa có admin thì tạo mới 1 admin
            if (userRepository.findByUsername("admin").isEmpty()) {
                Set<RoleEntity> roles = new HashSet<>();
                roles.add(roleRepository.findById(Role.ADMIN.name()).orElseThrow());
                UserEntity userEntity = UserEntity.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123456"))
                        .roles(roles)
                        .build();

                userRepository.save(userEntity);
                log.warn("admin user has been created with default password: admin please change it!");
            }
        };
    }
}
