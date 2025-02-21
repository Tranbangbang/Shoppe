package org.example.cy_shop.dto.request.auth;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityVerificationRequest {
    private Long accountId;
    private MultipartFile idCardImage;
    private String qrCodeData;
}