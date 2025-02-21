package org.example.cy_shop.dto.request.order.vnpay;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderIdsRequest {
    private List<Long> idOrder;
}
