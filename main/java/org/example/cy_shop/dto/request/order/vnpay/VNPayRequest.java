package org.example.cy_shop.dto.request.order.vnpay;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VNPayRequest {
    private Long idUser;
    private List<Long> idOrders;

    private Integer amount;
    private String orderInfo;
    private String urlDerect;

}
