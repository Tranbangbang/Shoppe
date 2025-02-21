package org.example.cy_shop.dto.response.order;

import lombok.*;
import org.example.cy_shop.enums.order.StatusOrderEnum;
import org.example.cy_shop.enums.user.TypeUserEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrackingResponse {
    private Long id;
    private StatusOrderEnum status;
    private String note;
    private TypeUserEnum userUpdateStatus;
    private Long idOrder;
}
