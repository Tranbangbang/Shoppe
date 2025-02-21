package org.example.cy_shop.dto.response.product.stock_response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LittleOptionResponse {
    private String optionName;
    private List<String> optionValue;
}
