package org.example.cy_shop.dto.response.product;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductStatisticsResponse {
    private String productName;
    private Long sales;
}
