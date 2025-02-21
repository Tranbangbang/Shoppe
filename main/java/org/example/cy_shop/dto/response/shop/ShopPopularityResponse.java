package org.example.cy_shop.dto.response.shop;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopPopularityResponse {
    private Long activeShops;
    private Long inactiveShops;
    private Long newShops;
}
