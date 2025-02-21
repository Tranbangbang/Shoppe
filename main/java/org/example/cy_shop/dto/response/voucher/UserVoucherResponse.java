package org.example.cy_shop.dto.response.voucher;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVoucherResponse {
    private Long id;
    private VoucherResponse voucher;
    private LocalDateTime usedAt;
    private Boolean isValid;
}
