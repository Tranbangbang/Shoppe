package org.example.cy_shop.dto.request.order;

import lombok.*;
import org.example.cy_shop.controller.client.product.ClientProductController;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderKeyRequest {
    private Long idOrder;
    String note;
}

//ClientProductController