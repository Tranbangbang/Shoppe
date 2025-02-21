package org.example.cy_shop.dto.request.cart;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OptionCartRequest {
    private String optionName;
    private String optionValue;
}
