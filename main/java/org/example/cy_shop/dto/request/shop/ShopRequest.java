package org.example.cy_shop.dto.request.shop;

import lombok.*;
import org.example.cy_shop.dto.request.address.AddressRequest;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopRequest {
    private Long id;
    //@NotBlank(message = "Shop name cannot be blank")
    //@Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Shop name must not contain special characters")
    private String shopName;
    private AddressRequest address;
}
