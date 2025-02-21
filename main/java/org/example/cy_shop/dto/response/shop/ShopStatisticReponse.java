package org.example.cy_shop.dto.response.shop;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopStatisticReponse {
    private Long totalShop;
    private Long activeShops;
    private Long inactiveShops;
    private Long newShop;
}
