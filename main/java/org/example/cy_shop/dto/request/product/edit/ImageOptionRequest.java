package org.example.cy_shop.dto.request.product.edit;

import lombok.*;
import org.example.cy_shop.controller.seller.SellerStockController;
import org.example.cy_shop.dto.request.product.add.StockRequest;
import org.example.cy_shop.dto.response.product.ProductForEditResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageOptionRequest {
    private String optionName;
    private String optionValue;
    private String image;

    public void simpleValidate(){
        if(this.optionName != null)
            this.optionName = this.optionName;

        if(this.optionValue != null)
            this.optionValue = this.optionValue;
    }

//    ProductEditRequest
//    StockRequest
//    SellerStockController
}
