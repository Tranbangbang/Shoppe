package org.example.cy_shop.dto.request.order.add;

import lombok.*;
import org.example.cy_shop.dto.request.order.VariantDTO;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductOrderRequest {
    private Long idProduct;
    private List<VariantDTO> variants;
    private Long quantity;
    private String image;
//    private String message;

}


