package org.example.cy_shop.dto.request.order;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderKeyDTO {
    private Long idShop;
    private Long idOrder;
}
