package com.devteria.identity_service.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "role")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleEntity {
    @Id
    String code;
    String description;

    @ManyToMany
    @JoinTable(
            name = "role_permission", //tên bảng trung gian
            joinColumns = @JoinColumn(name = "role_code"),
            inverseJoinColumns = @JoinColumn(name = "permission_code")
    )
    Set<PermissionEntity> permissions = new HashSet<>();

    @ManyToMany(mappedBy = "roles")
    Set<UserEntity> users = new HashSet<>();
}
