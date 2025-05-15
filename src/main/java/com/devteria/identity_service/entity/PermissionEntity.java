package com.devteria.identity_service.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "permission")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionEntity {
    @Id
    String code;

    String description;

    @ManyToMany(mappedBy = "permissions")
    //    @JsonIgnore
    Set<RoleEntity> roles = new HashSet<>();
}
