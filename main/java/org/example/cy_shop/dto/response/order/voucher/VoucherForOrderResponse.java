package org.example.cy_shop.dto.response.order.voucher;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoucherForOrderResponse {
    private Double lastPrice;
    private Double discountVoucher;
    private Boolean useVoucher;
}
