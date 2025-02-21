package org.example.cy_shop.dto.request.order;

import lombok.*;
import org.example.cy_shop.dto.request.product.edit.ProductEditRequest;
import org.example.cy_shop.service.order.IOrderService;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariantDTO {
    private String optionName;
    private String optionValue;

//    ProductEditRequest
//    IOrderService
}
