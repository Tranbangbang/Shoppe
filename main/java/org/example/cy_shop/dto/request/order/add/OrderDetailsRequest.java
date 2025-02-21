package org.example.cy_shop.dto.request.order.add;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailsRequest {
    private String message;
    private Long idShop;
    private Long idUserVoucher;
    private List<ProductOrderRequest> productOrders;

//    IOrderService
}
