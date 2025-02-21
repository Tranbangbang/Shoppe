package org.example.cy_shop.dto.request.cart;

import lombok.*;
import org.example.cy_shop.dto.request.order.VariantDTO;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartRequest {
    private Long id;
    private Long idProduct;
    private Long quantity;
    private List<VariantDTO> option;
}
