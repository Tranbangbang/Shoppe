package org.example.cy_shop.dto.request.search;

import lombok.*;
import org.example.cy_shop.enums.order.StatusOrderEnum;
import org.example.cy_shop.enums.order.StatusPaymentEnum;
import org.example.cy_shop.enums.order.TypePaymenEnum;
import org.example.cy_shop.utils.Const;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchOrder {
    private String email;
    private StatusOrderEnum orderStatus;
    private StatusPaymentEnum typePayment;
    private String keySearch;

    private Long idTransport;
    private Long idShop;

    private LocalDate startDate;
    private LocalDate endDate;

    public void simpleValidate() {
        if(email != null)
            email = email.trim();
        if(keySearch != null)
            keySearch = keySearch.trim();
    }
}
