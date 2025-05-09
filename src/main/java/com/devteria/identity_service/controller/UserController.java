package com.devteria.identity_service.controller;

import com.devteria.identity_service.dto.reponse.ApiResponse;
import com.devteria.identity_service.dto.reponse.UserResponse;
import com.devteria.identity_service.dto.request.UserCreationRequest;
import com.devteria.identity_service.dto.request.UserUpdateRequest;
import com.devteria.identity_service.entity.UserEntity;
import com.devteria.identity_service.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping()
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping("/myInfo")
    public ApiResponse<UserResponse> getMyInfo() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    //Lấy danh sách tất cả người dùng (chỉ ADMIN được phép)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ApiResponse<List<UserResponse>> getAllUsers() {
        // Lấy thông tin đang ddang nhập hiện tại
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        log.info("username: {}", authentication.getName());
//        authentication.getAuthorities().stream().forEach(grantedAuthority -> log.info(grantedAuthority.getAuthority()));

        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getAllUsers())
                .build();
    }

    //Lấy thông tin người dùng theo ID (chỉ ADMIN hoặc chính người dùng đó)
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUserById(@PathVariable("userId") String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUserById(userId))
                .build();
    }

    // Cập nhật thông tin người dùng (chỉ ADMIN hoặc chính người dùng, kiểm tra tham số)
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    @PutMapping("/{userId}")
    public UserResponse updateUser(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        return userService.updateUser(userId, request);
    }

//    // 4. Xóa người dùng (chỉ ADMIN, sử dụng bean để kiểm tra logic phức tạp)
//    @PreAuthorize("@userSecurityService.canDeleteUser(#id, authentication)")
    // 4. Xóa người dùng (chỉ ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return "User has been deleted";
    }
}
