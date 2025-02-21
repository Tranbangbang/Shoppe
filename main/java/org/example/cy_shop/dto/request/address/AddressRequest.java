package org.example.cy_shop.dto.request.address;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String province;
    private String district;
    private String ward;
    private String detailedAddress;
}
// check dieu kien dau vao cua request, cac truong string kh cho nhap ki tu dac biet, so dien thoai khong duoc la so am
