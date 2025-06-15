package com.devteria.identity_service.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;

import com.devteria.identity_service.validator.DobContraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
    @Size(min = 5, message = "USERNAME_INVALID")
    String username;

    @Size(min = 8, message = "INVALID_PASSWORD")
    String password;

    String firstName;
    String lastName;

    @DobContraint(min = 15, message = "INVALID_DOB")
    LocalDate dob;
}
