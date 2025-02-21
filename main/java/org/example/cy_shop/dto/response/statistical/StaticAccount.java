package org.example.cy_shop.dto.response.statistical;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StaticAccount {
    private Long totalAcc;
    private Long activeAcc;
    private Long blockAcc;
    private Long todayNewAcc;
//    private Long previousDateAcc;
    private Double percentNewAcc;
}
