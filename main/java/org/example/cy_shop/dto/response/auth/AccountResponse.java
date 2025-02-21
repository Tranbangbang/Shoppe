package org.example.cy_shop.dto.response.auth;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponse {
    private Long idUser;

    private Long id;

    private String username;

    private String name;

    private String email;

    private String avatar;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Boolean isActive;

    private List<String> role;
}
