package org.example.cy_shop.dto.response.product.stock_response;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.cy_shop.dto.response.BaseResponse;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class StockResponse extends BaseResponse {
    private Long id;
    private Long quantity;
    private Double price;

    private Long idProduct;
    private String productName;
}
