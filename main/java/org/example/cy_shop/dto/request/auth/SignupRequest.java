package org.example.cy_shop.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignupRequest {
    @NotBlank
    String username;
    //String name;
    @NotBlank
    String email;
    String code;
    @NotBlank
    String password;
    @NotBlank
    LocalDate dob;
}
