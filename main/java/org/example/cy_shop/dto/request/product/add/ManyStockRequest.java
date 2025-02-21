package org.example.cy_shop.dto.request.product.add;

import lombok.*;
import org.example.cy_shop.dto.request.product.edit.ImageOptionRequest;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ManyStockRequest {
    private Long idProduct = 1L;
    private Long baseQuantity = 0L;
    private Double basePrice = 0.0;

    //-------chứa nhiều stock
    private List<StockRequest> stockRequestList = new ArrayList<>();
    private List<ImageOptionRequest> imageInfo = new ArrayList<>();
}
