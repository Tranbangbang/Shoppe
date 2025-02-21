package org.example.cy_shop.dto.response.cart;

import lombok.*;
import org.example.cy_shop.dto.request.order.VariantDTO;
import org.example.cy_shop.enums.StatusCartEnum;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {
    private Long id;
    private Long productId;
    private String productName;

    private Long quantityCart;
    private Long allQuantity;
    private Double priceEach;
    private String imageVariant;

    private Long idShop;
    private String shopName;

    //----1 sản phẩm có nhiều tùy chọn (mầu sắc, kích thước)
    private List<VariantDTO> option;
    private StatusCartEnum status;
}
