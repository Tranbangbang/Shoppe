package org.example.cy_shop.dto.response.product.stock_response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ProductSearchResponse {
    private Long id;
    private String productName;
    private String productCode;
    private String productDescription;

    private List<String> detailImage;
    private String introVideo;
    private String coverImage;

    private Long categoryId;
    private String categoryName;

    private Long shopId;
    private String shopName;

    private Boolean isActive;
    private Boolean isBan;
    private Boolean isDelete;
    private Long allQuantity;

    private Double minPrice;
    private Double maxPrice;
}
