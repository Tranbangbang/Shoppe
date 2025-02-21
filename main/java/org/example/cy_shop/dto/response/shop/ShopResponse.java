package org.example.cy_shop.dto.response.shop;

import lombok.*;
import org.example.cy_shop.dto.response.address.AddressResponse;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopResponse {
    private Long id;
    private String shopName;
    private Boolean isApproved;
    private AddressResponse address;
}
