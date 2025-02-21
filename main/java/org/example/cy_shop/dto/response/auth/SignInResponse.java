package org.example.cy_shop.dto.response.auth;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Builder
public class SignInResponse {
    private Long id;
    private String type = "Bearer";
    private String accountToken;
    private String refreshToken;
    private String username;
    private String email;
    private Boolean isActive;
    private String avatar;
    private List<String> roleName;
}
