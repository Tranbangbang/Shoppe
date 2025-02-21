package org.example.cy_shop.dto.response.banner;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromotionalBannerResponse {
    private Long id;
    private String name;
    private String image;
    private LocalDateTime createdAt;
}
