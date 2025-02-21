package org.example.cy_shop.dto.request.product.add;

import lombok.*;
import org.example.cy_shop.dto.request.product.OptionValueRequest;
import org.example.cy_shop.dto.request.product.edit.ImageOptionRequest;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockRequest {
    private Long quantity;
    private Double price;
//    private Long idProduct;

    //----------nhiều value cùng có 1 stockId
    private List<OptionValueRequest> optionValueRequest;
//    private Long versionUpdate;
//    private List<ImageOptionRequest> imageInfo = new ArrayList<>();

}
