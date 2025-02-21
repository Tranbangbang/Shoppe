package org.example.cy_shop.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopShopResponse {
    private Long shopId;
    private String shopName;
    private Double revenue;
}
