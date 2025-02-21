package org.example.cy_shop.dto.request.product.delete;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductKeysRequest {
    private List<ProductKeyRequest> idProducts;
}
