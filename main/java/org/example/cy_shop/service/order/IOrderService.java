package org.example.cy_shop.service.order;

import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.order.OrderKeyDTO;
import org.example.cy_shop.dto.request.order.vnpay.OrderIdsRequest;
import org.example.cy_shop.dto.request.search.SearchOrder;
import org.example.cy_shop.dto.response.order.OrderShopResponse;
import org.example.cy_shop.dto.response.order.StaticOrderResponse;
import org.example.cy_shop.dto.response.order.shop.OrderOfShopResponse;
import org.example.cy_shop.entity.order.OrderEntity;

import java.time.LocalDate;
import org.example.cy_shop.dto.response.shop.ShopRevenueDTO;

import java.util.List;
import org.example.cy_shop.dto.request.order.OrderKeyRequest;
import org.example.cy_shop.dto.request.order.add.OrderDetailsRequest;
import org.example.cy_shop.dto.request.order.add.OrderRequest;
import org.example.cy_shop.enums.order.StatusOrderEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IOrderService {
    //----lưu order base
    ApiResponse<Long> addOneOrder(OrderRequest orderRequest);

    //----lưu 1 đơn hàng (1 shop)
    ApiResponse<OrderKeyDTO> addOneOrderDetails(OrderDetailsRequest request, Long orderId) throws InterruptedException;

    //---- mua nhiều  (nhiều shop)
    ApiResponse<List<OrderKeyDTO>> buyMany(OrderRequest request) throws InterruptedException;


    //List Order by user
    ApiResponse<List<OrderShopResponse>> getListOrderByUser(String productName, Pageable pageable);

    ApiResponse<List<OrderShopResponse>> getListOrderByUser(String productName, StatusOrderEnum status, Pageable pageable);

    ApiResponse<OrderShopResponse> getOrderByUser(Long id);

    ApiResponse<List<ShopRevenueDTO>> getTop5Shops(LocalDate startDate, LocalDate endDate);
    //----Hủy đơn hàng (user)
    ApiResponse<String> userCancelledOrder(OrderKeyRequest request);

    //----user xác nhận đơn hàng (nhận được => đã thanh toán, không nhận được, trả hàng => hoàn tiền)
    ApiResponse<String> userChangeStatusOrder(OrderKeyRequest request, StatusOrderEnum status);

    //----shop accept đơn hàng
    ApiResponse<String> adminAcceptStatusOrder(OrderKeyRequest request);

    List<OrderEntity> getListOrderByStatus(Long idShop, StatusOrderEnum status,  LocalDate start, LocalDate end);

    //-----thong ke
    ApiResponse<StaticOrderResponse> staticOrderMyShop(LocalDate startDate, LocalDate endDate);

    //-----cập nhật thanh toán thành công
    ApiResponse<String> payVNPaySuccess(OrderIdsRequest request);

    //------đơn hàng shop
    ApiResponse<Page<OrderOfShopResponse>> getMyOrderShop(SearchOrder searchOrder, Pageable pageable);

    //---tăng số lượng sản phẩm trong kho nếu đơn hàng cancel
    void updateStock(OrderEntity entity);






}
