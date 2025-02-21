package org.example.cy_shop.dto.request.order.status;

import jakarta.validation.constraints.NegativeOrZero;
import lombok.*;
import org.example.cy_shop.dto.request.order.OrderKeyRequest;
import org.example.cy_shop.enums.order.StatusOrderEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatusOrderRequest {
    private Long idOrder;
    String note;

    private StatusOrderEnum statusOrder;

}
