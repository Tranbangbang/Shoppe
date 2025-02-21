package org.example.cy_shop.dto.response.order;

import lombok.*;
import org.example.cy_shop.dto.response.auth.AccountResponse;
import org.example.cy_shop.enums.order.StatusOrderEnum;
import org.example.cy_shop.enums.order.StatusPaymentEnum;
import org.example.cy_shop.enums.order.TypePaymenEnum;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderShopResponse {
    private Long id;
    private String orderCode;
    private String shippingAdress;
    private StatusOrderEnum statusOrder;
    private StatusPaymentEnum statusPayment;
    private TypePaymenEnum typePayment;
    private Long idUserVoucher;
    private Long idShop;
    private AccountResponse account;
    private List<TrackingResponse> trackingResponses;
    private List<OrderDetailShopResponse> orderDetail;
    private Boolean isFeedback;

    private Double lastOrderPrice;
    private Double priceDiscount;
}
