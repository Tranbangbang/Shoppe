package org.example.cy_shop.mapper;

import org.example.cy_shop.dto.request.shop.ShopRequest;
import org.example.cy_shop.dto.response.auth.AccountResponse;
import org.example.cy_shop.dto.response.address.AddressResponse;
import org.example.cy_shop.dto.response.shop.ShopDetailResponse;
import org.example.cy_shop.dto.response.shop.ShopResponse;
import org.example.cy_shop.entity.Address;
import org.example.cy_shop.entity.Shop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ShopMapper {
    @Autowired
    AddressMapper mapper;
    @Autowired
    AccountMapper accountMapper;
    public ShopResponse toResponse(Shop shop){
        AddressResponse addressResponse = mapper.toResponse(shop.getDetailedAddress());
        return ShopResponse.builder()
                .id(shop.getId())
                .shopName(shop.getShopName())
                .isApproved(shop.getIsApproved())
                .address(addressResponse)
                .build();
    }

    public ShopDetailResponse toResponseDetail1(Shop shop){
        AddressResponse addressResponse = mapper.toResponse(shop.getDetailedAddress());
        AccountResponse accountResponse = accountMapper.toResponse(shop.getAccount());
        return ShopDetailResponse.builder()
                .id(shop.getId())
                .shopName(shop.getShopName())
                .isApproved(shop.getIsApproved())
                .account(accountResponse)
                .address(addressResponse)
                .build();
    }

    public List<ShopDetailResponse> toResponseDetail(List<Shop> shopList) {
        return shopList.stream()
                .map(this::toResponseDetail1)
                .collect(Collectors.toList());
    }
    public Shop toEntity(ShopRequest shopRequest){
        Address address = mapper.toEntity(shopRequest.getAddress());
        return Shop.builder()
                .id(shopRequest.getId())
                .shopName(shopRequest.getShopName())
                .detailedAddress(address)
                .build();
    }
}
