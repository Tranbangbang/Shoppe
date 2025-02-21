package org.example.cy_shop.dto.response.shop;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopRevenueResponse {
    private Double revenue;
    private Long order;
    private Long order_Cancelled;
    //private Long visits;
}
// thống kê theo ngày hôm nay