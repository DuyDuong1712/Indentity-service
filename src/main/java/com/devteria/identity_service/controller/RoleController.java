package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.reponse.ApiResponse;
import com.devteria.identity_service.dto.reponse.PermissionReponse;
import com.devteria.identity_service.dto.reponse.RoleReponse;
import com.devteria.identity_service.dto.reponse.UserResponse;
import com.devteria.identity_service.dto.request.PermissionRequest;
import com.devteria.identity_service.dto.request.RoleRequest;
import com.devteria.identity_service.dto.request.UserUpdateRequest;
import com.devteria.identity_service.service.PermissionService;
import com.devteria.identity_service.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
    RoleService roleService;

    @PostMapping()
    ApiResponse<RoleReponse> createRole(@RequestBody RoleRequest request) {
        return ApiResponse.<RoleReponse>builder()
                .result(roleService.createRole(request))
                .build();
    }

    @GetMapping()
    ApiResponse<List<RoleReponse>> getAllRoles() {
        return ApiResponse.<List<RoleReponse>>builder()
                .result(roleService.getAllRoles())
                .build();
    }

    @DeleteMapping("/{role}")
    ApiResponse deleteRole(@PathVariable("role") String role) {
        roleService.deleteRole(role);
        return ApiResponse.builder().build();
    }
}



