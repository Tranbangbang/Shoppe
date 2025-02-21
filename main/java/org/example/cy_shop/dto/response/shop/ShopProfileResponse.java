package org.example.cy_shop.dto.response.shop;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopProfileResponse {
    private Long slProduct;
    private Long follower;
    private String timeJoin;
    private Double rate;
    private Long slRate;
}
