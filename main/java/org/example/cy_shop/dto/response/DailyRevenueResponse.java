package org.example.cy_shop.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyRevenueResponse {
    private LocalDate date;
    private double totalRevenue;

}
