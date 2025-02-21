package org.example.cy_shop.dto.request.product.edit;

import lombok.*;
import org.example.cy_shop.dto.request.product.OptionValueRequest;
import org.example.cy_shop.dto.request.product.add.ManyStockRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductEditRequest {
    private Long id;
    private String productName;
    private String productDescription;

//    private MultipartFile[] detailImage;
//    private MultipartFile introVideo;
//    private MultipartFile coverImage;

    private List<String> detailImage;
    private String introVideo;
    private String coverImage;

    private Long categoryId;

    private ManyStockRequest stocks;
//    private List<ImageOptionRequest> imageInfo;

    public void simpleValidate(){
        if(this.productName != null)
            this.productName = this.productName.trim();
        if(this.productDescription != null)
            this.productDescription = this.productDescription.trim();

    }
}
