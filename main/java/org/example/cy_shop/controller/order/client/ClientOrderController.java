package org.example.cy_shop.controller.order.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.order.OrderKeyDTO;
import org.example.cy_shop.dto.request.order.status.StatusOrderRequest;
import org.example.cy_shop.dto.response.order.OrderShopResponse;
import org.example.cy_shop.enums.order.StatusOrderEnum;
import org.example.cy_shop.dto.request.order.OrderKeyRequest;
import org.example.cy_shop.dto.request.order.add.OrderRequest;
import org.example.cy_shop.service.order.IOrderService;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "ORDER_03. ORDER")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/client_order")
public class ClientOrderController {
    @Autowired
    IOrderService orderService;


    //---mua hàng
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @PostMapping("/buy")
    @Operation(
            summary = "mua hàng",
            description = "**Mua 1/nhiều sản phẩm thừ 1/nhiều shop**"
    )
    public ApiResponse<List<OrderKeyDTO>> addOne(@RequestBody OrderRequest orderRequest) throws InterruptedException {
        return orderService.buyMany(orderRequest);
    }

    //@PreAuthorize("hasAnyRole('ROLE_USER')")
    @GetMapping("/list")
    public ApiResponse<List<OrderShopResponse>> getListOrderByUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String productName
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return orderService.getListOrderByUser(productName, pageable);
    }

    @GetMapping("/search")
    public ApiResponse<List<OrderShopResponse>> getListOrderByUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) StatusOrderEnum status
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return orderService.getListOrderByUser(productName, status, pageable);
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderShopResponse> getOrderById(@PathVariable Long id) {
        return orderService.getOrderByUser(id);
    }



    //@PreAuthorize("hasAnyRole('ROLE_SHOP')")
    @PostMapping("/cancel")
    public ApiResponse<String> cancelled(@RequestBody OrderKeyRequest request){
        return orderService.userCancelledOrder(request);
    }

    //---xác nhận sản phẩm (nhận được, trả hàng, không nhận được)
    @Operation(
            summary = "Xác nhận trạng thái hàng",
            description = "**Nhận được, trả hàng, không nhận được**"
    )
    @PreAuthorize("hasAnyRole('ROLE_USER')")
    @PostMapping("/user_change_status")
    public ApiResponse<String> changeStatusOrder(@RequestBody StatusOrderRequest request){
        OrderKeyRequest orderKeyRequest = new OrderKeyRequest(request.getIdOrder(), request.getNote());
        return orderService.userChangeStatusOrder(orderKeyRequest, request.getStatusOrder());
    }
}
