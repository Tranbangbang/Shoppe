package org.example.cy_shop.dto.request.banner;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePromotionalBannerRequest {
    private Long id;
    private String name;
    private String image;
}
