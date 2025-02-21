package org.example.cy_shop.dto.response.product.stock_response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValueResponse {
    private Long id;
    private String optionValue;

    private Long idOption;
    private String optionName;

    private Long idStock;
    private Long versionUpdate;

    private Long quantity;
    private Double price;
}
