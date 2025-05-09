package com.devteria.identity_service.dto.reponse;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleReponse {
    String code;
    String description;
    Set<PermissionReponse> permissions;
}
