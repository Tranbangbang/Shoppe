package org.example.cy_shop.dto.request.search;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityVerificationData {
    private String fullName;
    private String identityNumber;
    private String gender;
    private LocalDate birthDate;
    private String address;
}
