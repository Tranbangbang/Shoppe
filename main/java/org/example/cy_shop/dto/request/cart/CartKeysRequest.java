package org.example.cy_shop.dto.request.cart;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartKeysRequest {
    private List<CartKeyRequest> keys;
}
