package org.example.cy_shop.controller.order.vnpay;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.order.OrderKeyRequest;
import org.example.cy_shop.dto.request.order.vnpay.OrderIdsRequest;
import org.example.cy_shop.dto.request.order.vnpay.VNPayRequest;
import org.example.cy_shop.service.impl.order.OrderService;
import org.example.cy_shop.service.order.IOrderService;
import org.example.cy_shop.service.order.IVNPayService;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@Tag(name = "ORDER_04. VNPay")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/client_vnpay")
public class ClientVNPayController {
    @Autowired
    IVNPayService vnPayService;
    @Autowired
    private OrderService orderService;

    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @PostMapping("/user_payment")
    @Operation(
            summary = "Thanh toán đơn hàng",
            description = "**Thanh toán cho 1 list đơn hàng (từ 1 list order, FE lấy ra được số tiền để thanh toán)**"
    )
    public String getUrl(@RequestBody VNPayRequest vnPayRequest) throws UnsupportedEncodingException {
        String url = vnPayService.createOrder(vnPayRequest.getAmount(), vnPayRequest.getOrderInfo(), vnPayRequest.getUrlDerect());

        return url;
    }

    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @PostMapping("/use_payVN_success")
    @Operation(
            summary = "Cập nhật đơn hàng sau khi thanh toán",
            description = "**Đơn hàng sau khi thanh toán, sẽ được cập nhật trạng thái là paid**"
    )
    public ApiResponse<String> updatePay(@RequestBody OrderIdsRequest request){
        try {
            orderService.payVNPaySuccess(request);
            return new ApiResponse<>(200, "Thanh toán thành công", null);
        }catch (Exception e){
            System.out.println("Loi thanh toan (client controller): " + e);
            return new ApiResponse<>(400, "Không thể thanh toán", e.getMessage());
        }
    }

//    IOrderService
}
