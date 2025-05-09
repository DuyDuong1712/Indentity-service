package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.reponse.ApiResponse;
import com.devteria.identity_service.dto.reponse.PermissionReponse;
import com.devteria.identity_service.dto.request.PermissionRequest;
import com.devteria.identity_service.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {
    PermissionService permissionService;

    @PostMapping()
    ApiResponse<PermissionReponse> createPermission(@RequestBody PermissionRequest permissionRequest) {
        return ApiResponse.<PermissionReponse>builder()
                .result(permissionService.createPermission(permissionRequest))
                .build();
    }

    @GetMapping()
    ApiResponse<List<PermissionReponse>> getAllPermissions() {
        return ApiResponse.<List<PermissionReponse>>builder()
                .result(permissionService.getAllPermission())
                .build();
    }

    @DeleteMapping("/{permission}")
    ApiResponse deletePermission(@PathVariable("permission") String permission) {
        permissionService.deletePermission(permission);
        return ApiResponse.builder().build();
    }
}
