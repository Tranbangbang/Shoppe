package org.example.cy_shop.dto.request.cart.update;

import lombok.*;
import org.example.cy_shop.dto.request.order.VariantDTO;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartUpdateRequest {
    private Long id;
    private Long quantity;
    private List<VariantDTO> variants;
}
