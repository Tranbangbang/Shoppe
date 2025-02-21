package org.example.cy_shop.dto.request;

import lombok.*;
import org.example.cy_shop.service.IAccountService;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationUserRequest {
    private String province;
    private String district;
    private String ward;
    private String detailedAddress;

//    IAccountService
}
