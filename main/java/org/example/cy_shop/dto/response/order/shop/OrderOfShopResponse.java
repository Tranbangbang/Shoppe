package org.example.cy_shop.dto.response.order.shop;

import lombok.*;
import org.example.cy_shop.controller.order.client.ClientCartController;
import org.example.cy_shop.controller.seller.SellerStockController;
import org.example.cy_shop.dto.response.cart.StockInfo;
import org.example.cy_shop.enums.order.StatusOrderEnum;
import org.example.cy_shop.enums.order.StatusPaymentEnum;
import org.example.cy_shop.enums.order.TypePaymenEnum;
import org.example.cy_shop.service.impl.order.OrderSchedule;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderOfShopResponse {
    private Long id;
    private Long idShop;
    private String shopName;

    private String orderCode;
    private String shippingAdress;
    private String userName;
    private String email;

    private StatusOrderEnum orderStatus;
    private TypePaymenEnum typePayment;
    private StatusPaymentEnum statusPayment;

    private String message;
//    private Double allPrice;

    private List<OrderDetailsOfShopResponse> orderDetails;

}

//OrderSchedule
