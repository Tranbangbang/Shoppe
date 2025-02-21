package org.example.cy_shop.dto.response.product;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRecommendationResponse {
    private Long productId;
    private String productName;
    private Long viewCount;
}
