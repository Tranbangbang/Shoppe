package org.example.cy_shop.dto.response.cart;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OptionCartResponse {
    private String optionName;
    private String optionValue;
}
