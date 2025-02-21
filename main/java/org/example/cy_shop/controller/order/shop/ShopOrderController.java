package org.example.cy_shop.controller.order.shop;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.order.OrderKeyRequest;
import org.example.cy_shop.dto.request.search.SearchOrder;
import org.example.cy_shop.dto.response.order.StaticOrderResponse;
import org.example.cy_shop.dto.response.order.shop.OrderOfShopResponse;
import org.example.cy_shop.entity.order.OrderEntity;
import org.example.cy_shop.enums.order.StatusOrderEnum;
import org.example.cy_shop.enums.order.StatusPaymentEnum;
import org.example.cy_shop.enums.order.TypePaymenEnum;
import org.example.cy_shop.enums.user.TypeUserEnum;
import org.example.cy_shop.mapper.order.OrderMapper;
import org.example.cy_shop.service.impl.order.OrderService;
import org.example.cy_shop.service.order.IOrderService;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Tag(name = "ORDER_04. STATIC_ORDER")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/admin_order")
public class ShopOrderController {
    @Autowired
    IOrderService orderService;

//    @PreAuthorize("hasAnyRole('ROLE_SHOP')")
//    @PostMapping("/cancel")
//    public ApiResponse<String> cancel(@RequestBody OrderKeyRequest request) {
//        return orderService.userCancelledOrder(request, TypeUserEnum.SHOP);
//    }

    //---admin xác nhận đơn hàng
    @Operation(
            summary = "Admin xác nhận đơn hàng"
    )
    @PreAuthorize("hasAnyRole('ROLE_SHOP')")
    @PostMapping("/accept")
    public ApiResponse<String> accept(@RequestBody OrderKeyRequest request) {
        return orderService.adminAcceptStatusOrder(request);
    }

    //---admin list ra đơn hàng
    @Operation(
            summary = "Đơn hàng của shop",
            description = "**Danh sách đơn hàng của shop (có phân trang và lọc). " +
                    "Nếu truyền ngày tháng, cần định dạng đúng dd/MM/yyyy." +
                    "KeySearch (tìm theo tên sản phẩm, email hoặc userName)**.\n" +
                    " PENDING, ACCEPT,RECEIVED, RETURNED, NOT_RECEIVED, CANCELED, REFUNDED"
    )
    @PreAuthorize("hasAnyRole('ROLE_SHOP')")
    @GetMapping("/my_shop_order")
    public ApiResponse<Page<OrderOfShopResponse>> findMyShopOrder(@RequestParam(name = "page", required = false) Integer page,
                                                                  @RequestParam(name = "limit", required = false) Integer limit,
                                                                  @RequestParam(name = "orderStatus", required = false) String orderStatus,
                                                                  @RequestParam(name = "keySearch", required = false) String keySearch,
                                                                  @RequestParam(name = "startDate", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,
                                                                  @RequestParam(name = "endDate", required = false) @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate) {

        //id shop được set trong service
        StatusOrderEnum orderStatusEnum = null;
        StatusPaymentEnum statusPaymentEnum = null;

        if(orderStatus != null)
            orderStatus = orderStatus.trim();

        try {
            if(orderStatus != null) {
                if (orderStatus.equalsIgnoreCase("REFUNDED"))
                    statusPaymentEnum = StatusPaymentEnum.valueOf(orderStatus);
                else
                    orderStatusEnum = StatusOrderEnum.valueOf(orderStatus);
            }
        }catch (Exception e){
            System.out.println("Khong the convert status search ");
        }
        SearchOrder searchOrder = SearchOrder.builder()
                .orderStatus(orderStatusEnum)
                .typePayment(statusPaymentEnum)
                .keySearch(keySearch)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        searchOrder.simpleValidate();

        //----xu lý page và limit
        if (page == null)
            page = 1;
        if (limit == null)
            limit = Const.RECORD_OF_TABLE_PAGE;

//        OrderEntity
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Order.desc("createDate")));

        return orderService.getMyOrderShop(searchOrder, pageable);
    }

    //---thống kê của admin (phần đầu)
    @Operation(
            summary = "Thống kê của shop (phần đầu)",
            description = ""
    )
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_SHOP')")
    @GetMapping("/static_shop_header")
    public ApiResponse<StaticOrderResponse> getStaticOrderResponse(
            @RequestParam(name = "startDate", required = false)
            @DateTimeFormat(pattern = "dd/MM/yyyy")
            LocalDate start,
            @RequestParam(name = "endDate", required = false)
            @DateTimeFormat(pattern = "dd/MM/yyyy")
            LocalDate end) {

        return orderService.staticOrderMyShop(start, end);
    }
}
