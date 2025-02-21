package org.example.cy_shop.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardStatisticsResponse {
    private Double totalRevenue;
    private Long totalOrders;
    private Long totalShops;
    private Long activeUsers;
}
