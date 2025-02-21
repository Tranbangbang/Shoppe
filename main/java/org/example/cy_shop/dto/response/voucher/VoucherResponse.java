package org.example.cy_shop.dto.response.voucher;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherResponse {
    private Long id;
    private String voucherCode;
    private String discountType;
    private Double discountValue;
    private Double minOrderValue;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer maxUsage;
    private Boolean isActive;
    private LocalDateTime createAt;
    private LocalDateTime updatedAt;
}
