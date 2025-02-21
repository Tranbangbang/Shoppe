package org.example.cy_shop.dto.response.product.stock_response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OptionResponse {
    private Long id;
    private String optionName;
    private Long idProduct;

    private List<ValueResponse> valueResponseList;
}
