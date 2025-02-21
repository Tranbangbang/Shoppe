package org.example.cy_shop.dto.response.auth;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private String username;
    private Long accountId;
    private Long shopId;
    private Boolean isApprovedShop;
    private String name;
    private LocalDate dob;
    private String avatar;
    private int gender;
    private String location;
    private String email;
    private List<String> role;
}
