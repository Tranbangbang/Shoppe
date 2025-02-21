package org.example.cy_shop.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SignInRequest {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}