package org.example.cy_shop.dto.response.product.stock_response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OneParentOptionResponse {
    private String optionName;
    private String optionValue;
    private Long quantity;
    private Double price;
    private String img;
    List<OneChildrenOptionResponse> options;
}
