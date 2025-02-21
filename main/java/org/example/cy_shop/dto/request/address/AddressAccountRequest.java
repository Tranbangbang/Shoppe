package org.example.cy_shop.dto.request.address;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressAccountRequest {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String province;
    private String district;
    private String ward;
    private String detailedAddress;
}
