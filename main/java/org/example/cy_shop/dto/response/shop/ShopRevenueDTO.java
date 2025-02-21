package org.example.cy_shop.dto.response.shop;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopRevenueDTO {
    private Long shopId;
    private Double revenue;
    //'YYYY-MM-DD HH:mm:ss.SSSSSS'
}
