package org.example.cy_shop.dto.request.report;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {
    private Long product_id;
    private String reason;
}
