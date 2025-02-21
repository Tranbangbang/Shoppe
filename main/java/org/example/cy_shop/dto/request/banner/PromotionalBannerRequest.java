package org.example.cy_shop.dto.request.banner;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionalBannerRequest {
    private Long id;
    private String name;
    private MultipartFile image;
    //private LocalDateTime createdAt;
}
