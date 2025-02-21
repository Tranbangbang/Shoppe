package org.example.cy_shop.mapper;

import org.example.cy_shop.dto.request.address.AddressRequest;
import org.example.cy_shop.dto.response.address.AddressResponse;
import org.example.cy_shop.entity.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {
    public AddressResponse toResponse(Address address){
        return AddressResponse.builder()
                .id(address.getId())
                .fullName(address.getFullName())
                .phoneNumber(address.getPhoneNumber())
                .province(address.getProvince())
                .district(address.getDistrict())
                .ward(address.getWard())
                .detailedAddress(address.getDetailedAddress())
                .build();
    }
    public Address toEntity(AddressRequest addressRequest){
        return Address.builder()
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
