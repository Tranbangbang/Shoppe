package org.example.cy_shop.dto.response.product;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.cy_shop.dto.response.product.stock_response.OneParentOptionResponse;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ProductDetailForEditResponse {
    private ProductResponse product;

//    private List<OptionResponse> optionResponseList;
    private List<OneParentOptionResponse> option;
}
