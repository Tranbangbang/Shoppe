package org.example.cy_shop.dto.response.order;

import lombok.*;
import org.example.cy_shop.dto.request.order.VariantDTO;
import org.example.cy_shop.dto.response.product.ProductResponse;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailShopResponse {
    private Long id;
    private List<VariantDTO> variant;
    private String image;
    private Long quantity;
    private Double price;
    private String message;
    private Double lastPrice;
    private ProductResponse productResponse;
}
