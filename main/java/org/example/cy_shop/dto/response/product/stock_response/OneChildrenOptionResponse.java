package org.example.cy_shop.dto.response.product.stock_response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OneChildrenOptionResponse {
    private String optionName;
    private String optionValue;
    private Long quantity;
    private Double price;
    private String img;
}
