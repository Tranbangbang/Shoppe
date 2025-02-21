package org.example.cy_shop.mapper.order.shop;

import org.example.cy_shop.dto.request.order.VariantDTO;
import org.example.cy_shop.dto.response.order.shop.OrderDetailsOfShopResponse;
import org.example.cy_shop.dto.response.order.shop.OrderOfShopResponse;
import org.example.cy_shop.entity.Shop;
import org.example.cy_shop.entity.order.OrderEntity;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.order.VariantMapper;
import org.example.cy_shop.repository.IShopRepository;
import org.example.cy_shop.service.impl.order.CartService;
import org.example.cy_shop.service.impl.product.ProductService;
import org.example.cy_shop.service.order.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderOfShopMapper {
    @Autowired
    VariantMapper variantMapper;
    @Autowired
    ProductService productService;
    @Autowired
    IShopRepository shopRepository;


    public OrderOfShopResponse convertToOrderShopResponse(OrderEntity entity){
        Shop shop = shopRepository.findById(entity.getIdShop()).orElse(null);
        if(shop == null)
            throw new AppException(ErrorCode.SHOP_NOT_FOUND);

        List<OrderDetailsOfShopResponse> list = convertToListOrderDetailsOfShopResponse(entity);

        return OrderOfShopResponse.builder()
                .id(entity.getId())
                .idShop(entity.getIdShop())
                .shopName(shop.getShopName())
                .orderCode(entity.getOrderCode())
                .shippingAdress(entity.getShippingAdress())
                .userName(entity.getAccount().getUser().getUsername())
                .email(entity.getAccount().getEmail())

                .orderStatus(entity.getStatusOrder())
                .typePayment(entity.getTypePayment())
                .statusPayment(entity.getStatusPayment())

                .message(entity.getMessage())

                .orderDetails(list)
                .build();
    }

    public List<OrderDetailsOfShopResponse> convertToListOrderDetailsOfShopResponse(OrderEntity entity){
        List<OrderDetailsOfShopResponse> result = new ArrayList<>();

        for (var it: entity.getOrderDetail()){
            List<VariantDTO> variantDTO = variantMapper.convertStringToDTO(it.getVariant());

            OrderDetailsOfShopResponse orderDetailTmp =
                    OrderDetailsOfShopResponse.builder()
                            .id(it.getId())
                            .idProduct(it.getProduct().getId())
                            .productName(it.getProduct().getProductName())
                            .image(it.getImage())
                            .price(it.getPrice())
                            .quantity(it.getQuantity())
                            .variantDTOS(variantDTO)
                            .lastPrice(it.getLastPrice())
                            .build();

            result.add(orderDetailTmp);
        }

        return result;
    }
}

//ICartService
