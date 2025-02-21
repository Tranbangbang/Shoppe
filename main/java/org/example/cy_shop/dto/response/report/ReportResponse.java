package org.example.cy_shop.dto.response.report;

import lombok.*;
import org.example.cy_shop.dto.response.auth.AccountResponse;
import org.example.cy_shop.dto.response.product.ProductResponse;
import org.example.cy_shop.enums.EnumReportStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportResponse {
    private Long id;
    private AccountResponse reporter;
    private ProductResponse product;
    private Long shop_id;
    private String reason;
    private String desc;
    private EnumReportStatus status ;
    private LocalDateTime createdAt;
}
