package org.example.cy_shop.dto.request.voucher;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoucherRequest {
    //private String voucherCode;
    private String discountType;
    private Double discountValue;
    private Double minOrderValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer maxUsage;
}
