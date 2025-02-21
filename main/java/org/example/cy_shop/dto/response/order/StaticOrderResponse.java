package org.example.cy_shop.dto.response.order;

import lombok.*;
import org.example.cy_shop.repository.product_repository.IProductRepository;
import org.example.cy_shop.service.order.IOrderService;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StaticOrderResponse {
    private Long countPending;
    private Long countAccept;
    private Long countCancelled;
    private Long countRefund;
    private Long countReport;
    private Long noStock;

//    IOrderService
//    IOrderService
//    IProductRepository
}
