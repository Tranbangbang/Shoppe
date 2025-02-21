package org.example.cy_shop.dto.response.product;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor

public class ProductShopStatisticsResponse {
    private Long productId;
    private String productName;
    private String productCode;
    private Double minPrice;
    private Double maxPrice;
    private String image;
    private Long quantitySold;

    public ProductShopStatisticsResponse(Long productId, String productName, String productCode,
                                         Double minPrice, Double maxPrice, String image, Long quantitySold) {
        this.productId = productId;
        this.productName = productName;
        this.productCode = productCode;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.image = image;
        this.quantitySold = quantitySold;
    }
}
