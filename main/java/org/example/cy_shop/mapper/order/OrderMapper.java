package org.example.cy_shop.mapper.order;

import org.example.cy_shop.dto.request.order.VariantDTO;
import org.example.cy_shop.dto.response.order.OrderDetailShopResponse;
import org.example.cy_shop.dto.response.order.OrderShopResponse;
import org.example.cy_shop.dto.request.order.add.OrderRequest;
import org.example.cy_shop.dto.request.order.add.ProductOrderRequest;
import org.example.cy_shop.dto.response.order.TrackingResponse;
import org.example.cy_shop.entity.order.OrderDetailEntity;
import org.example.cy_shop.entity.order.OrderEntity;
import org.example.cy_shop.entity.order.TrackingEntity;
import org.example.cy_shop.entity.product.ProductEntity;
import org.example.cy_shop.enums.order.StatusOrderEnum;
import org.example.cy_shop.enums.order.StatusPaymentEnum;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.AccountMapper;
import org.example.cy_shop.mapper.product_mapper.ProductMapper;
import org.example.cy_shop.repository.IUserVoucherRepository;
import org.example.cy_shop.repository.IVoucherRepository;
import org.example.cy_shop.service.IAccountService;
import org.example.cy_shop.service.order.IOrderService;
import org.example.cy_shop.service.product.IProductService;
import org.example.cy_shop.service.product.IStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    @Autowired
    IAccountService accountService;
    @Autowired
    IOrderService orderService;
    @Autowired
    VariantMapper variantMapper;
    @Autowired
    IStockService stockService;
    @Autowired
    IVoucherRepository voucherRepository;
    @Autowired
    IUserVoucherRepository userVoucherRepository;
    @Autowired
    IProductService productService;

    @Autowired
    AccountMapper accountMapper;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    TrackingMapper trackingMapper;

    //--thiếu id_voucher, id_shop
    public OrderEntity convertToOrderEntity(OrderRequest orderRequest){

        return OrderEntity.builder()
                .orderCode(OrderEntity.generateRandomCode())
//                .shippingAdress(orderRequest.getShipAddress())

                .province(orderRequest.getProvince())
                .district(orderRequest.getDistrict())
                .commune(orderRequest.getCommune())
                .detailAddress(orderRequest.getDetailAddress())

                .fullName(orderRequest.getFullName())
                .phoneNumber(orderRequest.getPhoneNumber())

                .statusOrder(StatusOrderEnum.PENDING)
                .statusPayment(StatusPaymentEnum.UNPAID)
                .typePayment(orderRequest.getTypePayment())
                .account(accountService.getMyAccount())
                .build();
    }

    public OrderDetailEntity convertToOrderDetailEntity(ProductOrderRequest request, Long idOder){
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(idOder);

        ProductEntity product = new ProductEntity();
        product.setId(request.getIdProduct());

        String variant = variantMapper.convertVariantToString(request.getVariants());

        return OrderDetailEntity.builder()
                .order(orderEntity)
                .product(product)
                .image(request.getImage())
                .variant(variant)
                .build();
    }


    public OrderShopResponse toResponse(OrderEntity order) {
        List<OrderDetailShopResponse> orderDetails = order.getOrderDetail().stream()
                .map(this::toOrderDetailResponse)
                .collect(Collectors.toList());
        List<TrackingResponse> trackingResponses = order.getTracking().stream()
                .map(this::toTrackingResponse)
                .collect(Collectors.toList());
        //List<VariantDTO> variantDTOS = variantMapper.convertStringToDTO(order.getOrderDetail().get().getVariant());
        return OrderShopResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .shippingAdress(order.getShippingAdress())
                .statusOrder(order.getStatusOrder())
                .statusPayment(order.getStatusPayment())
                .typePayment(order.getTypePayment())
                .idUserVoucher(order.getIdUserVoucher())
                .idShop(order.getIdShop())
                .account(accountMapper.toResponse(order.getAccount()))
                .orderDetail(orderDetails)
                .trackingResponses(trackingResponses)

                .lastOrderPrice(order.getLastPrice())
                .priceDiscount(order.getDiscountVoucher())
                .build();

    }

    private TrackingResponse toTrackingResponse(TrackingEntity trackingEntity) {
        return TrackingResponse.builder()
                .id(trackingEntity.getId())
                .status(trackingEntity.getStatus())
                .note(trackingEntity.getNote())
                .userUpdateStatus(trackingEntity.getUserUpdateStatus())
                .build();
    }

    private OrderDetailShopResponse toOrderDetailResponse(OrderDetailEntity orderDetail) {
        ProductEntity product = orderDetail.getProduct();
        if (product == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUNT);
        }
        List<VariantDTO> variantDTOS = variantMapper.convertStringToDTO(orderDetail.getVariant());
        return OrderDetailShopResponse.builder()
                .id(orderDetail.getId())
                .variant(variantDTOS)
                .image(orderDetail.getImage())
                .quantity(orderDetail.getQuantity())
                .price(orderDetail.getPrice())
//                .message(orderDetail.getMessage())
                .lastPrice(orderDetail.getLastPrice())
                .productResponse(productMapper.convertToResponse(product))
                .build();

    }

//    ClientOrderController
//    OrderService


}
