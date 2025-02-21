package org.example.cy_shop.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequest {
    @Schema(title = "Refresh Token", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxIiwidXNlcm5hbWUiOiJhZG1pbiIsInRva2VuX3R5cGUiOjEsImFjY291bnRfc3RhdHVzIjowLCJleHAiOjE3MzI1MjQ5NzcsImlhdCI6MTczMTkyMDE3N30.ki632m2fl7fiko17OJFfPyg8RmI_6OKHycTbf-Ut024uc4R-oVwF-JeesqVdtOQhKnvSX-YKRGwZ4Lvdur80hA")
    @NotBlank
    private String requestToken;
}