package org.example.cy_shop.dto.request.auth;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelAccountGoogle {
    private String avatar;
    String username;
    String name;
    String email;
    String code;
    String password;
    LocalDate dob;
    int gender;
}
