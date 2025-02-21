package org.example.cy_shop.mapper;

import org.example.cy_shop.dto.request.address.AddressAccountRequest;
import org.example.cy_shop.dto.response.address.AddressAccountResponse;
import org.example.cy_shop.entity.AddressAccount;
import org.springframework.stereotype.Component;

@Component
public class AddressAccountMapper {
    public AddressAccountResponse toResponse(AddressAccount address){
        return AddressAccountResponse.builder()
                .id(address.getId())
                .fullName(address.getFullName())
                .phoneNumber(address.getPhoneNumber())
                .province(address.getProvince())
                .district(address.getDistrict())
                .ward(address.getWard())
                .detailedAddress(address.getDetailedAddress())
                .build();
    }
    public AddressAccount toEntity(AddressAccountRequest addressRequest){
        return AddressAccount.builder()
                .id(addressRequest.getId())
                .fullName(addressRequest.getFullName())
                .phoneNumber(addressRequest.getPhoneNumber())
                .province(addressRequest.getProvince())
                .district(addressRequest.getDistrict())
                .ward(addressRequest.getWard())
                .detailedAddress(addressRequest.getDetailedAddress())
                .build();
    }
}
