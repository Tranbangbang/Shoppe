package org.example.cy_shop.dto.response.order.shop;

import lombok.*;
import org.example.cy_shop.dto.request.order.VariantDTO;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailsOfShopResponse {
    private Long id;
    private Long idProduct;
    private String productName;

    private String image;
    private Long quantity;
    private Double price;

    private List<VariantDTO> variantDTOS;
    private Double lastPrice;
}
