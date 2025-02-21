package org.example.cy_shop.dto.request.order.add;

import lombok.*;
import org.example.cy_shop.enums.order.StatusPaymentEnum;
import org.example.cy_shop.enums.order.TypePaymenEnum;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {
    private String province;
    private String district;
    private String commune;
    private String detailAddress;

    private String fullName;
    private String phoneNumber;

    private TypePaymenEnum typePayment;
    private List<OrderDetailsRequest> orderDetails;

//   IOrderService

}
