package org.example.cy_shop.dto.response.shop;

import lombok.*;
import org.example.cy_shop.dto.response.address.AddressResponse;
import org.example.cy_shop.dto.response.auth.AccountResponse;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopDetailResponse {
    private Long id;
    private String shopName;
    private AccountResponse account;
    private AddressResponse address;
    private Boolean isApproved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
