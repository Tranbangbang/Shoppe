package org.example.cy_shop.dto.request.auth;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
