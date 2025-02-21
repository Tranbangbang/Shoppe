package org.example.cy_shop.dto.response.auth;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityVerificationResponse {
    private String fullName;
    private String identityNumber;
    private String gender;
    private LocalDate birthDate;
    private String address;
    private String releaseDate;
    private Boolean isVerified;
}
