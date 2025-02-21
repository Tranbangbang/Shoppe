package org.example.cy_shop.dto.response.product;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.cy_shop.dto.response.BaseResponse;
import org.example.cy_shop.dto.response.feedback.CountFeedbackResponse;
import org.example.cy_shop.mapper.product_mapper.ProductMapper;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder

//-------------responsitory cho user xem
public class ProductResponse extends BaseResponse {
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

    private Double ratingAverage;
    private CountFeedbackResponse countFb;
    private Long countSeller;
}
//ProductMapper
