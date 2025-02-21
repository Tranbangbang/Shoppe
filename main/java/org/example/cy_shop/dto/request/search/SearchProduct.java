package org.example.cy_shop.dto.request.search;

import lombok.*;
import org.example.cy_shop.service.impl.order.OrderService;
import org.example.cy_shop.utils.Const;
import org.example.cy_shop.utils.UtilsFunction;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchProduct {
    private Integer page;
    private Integer limit;
    private Long shopId;
    private String keySearch;
    private String categoryName;
    private Long idSubCategory;
    private Long idParentCategory;
    private String place;
    private Double startPrice;
    private Double endPrice;
    private Long quantity;
    private Boolean active;
    private Long idCategory;
    private String sortBy;
    private Double rating;
    private List<Long> idCategories;
    List<String> places;
    private String status;

    public void simpleValide() {
        if (this.page == null)
            this.page = 1;

        if (this.limit == null)
            this.limit = Const.PRODUCT_QUANTITY_OF_PAGE;

        this.keySearch = UtilsFunction.convertParamToString(this.keySearch);
        this.categoryName = UtilsFunction.convertParamToString(this.categoryName);
        this.place = UtilsFunction.convertParamToString(this.place);
        this.sortBy = UtilsFunction.convertParamToString(this.sortBy);
        if (this.status != null)
            this.status = this.status.trim();
    }

    @Override
    public String toString() {
        return "SearchProductClient{" +
                "page=" + page +
                ", limit=" + limit +
                ", keySearch='" + keySearch + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", place='" + place + '\'' +
                ", startPrice=" + startPrice +
                ", endPrice=" + endPrice +
                '}';
    }
}

//OrderService