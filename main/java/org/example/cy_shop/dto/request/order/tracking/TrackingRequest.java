package org.example.cy_shop.dto.request.order.tracking;

import lombok.*;
import org.example.cy_shop.enums.order.StatusOrderEnum;
import org.example.cy_shop.enums.user.TypeUserEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrackingRequest {
    private StatusOrderEnum status;
    private String note;
    private TypeUserEnum userUpdateStatus;
    private Long idOrder;
}
