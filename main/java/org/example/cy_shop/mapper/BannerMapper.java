package org.example.cy_shop.mapper;

import org.example.cy_shop.dto.request.banner.BannerRequest;
import org.example.cy_shop.dto.request.banner.UpdateBannerRequest;
import org.example.cy_shop.dto.response.banner.BannerResponse;
import org.example.cy_shop.entity.Banner;
import org.springframework.stereotype.Component;

@Component
public class BannerMapper {
    public BannerResponse toResponse(Banner banner){
        return BannerResponse.builder()
                .id(banner.getId())
                .name(banner.getName())
                .orderNumber(banner.getOrderNumber())
                .image(banner.getImageUrl())
                //.createdAt(banner.getCreatedAt())
                .build();
    }

    public Banner toEntity(BannerRequest bannerRequest){
        return Banner.builder()
                .id(bannerRequest.getId())
                .name(bannerRequest.getName())
                .orderNumber(bannerRequest.getOrderNumber())
                .imageUrl(bannerRequest.getName())
                .build();
    }

    public Banner toEntity(UpdateBannerRequest bannerRequest){
        return Banner.builder()
                .id(bannerRequest.getId())
                .name(bannerRequest.getName())
                .orderNumber(bannerRequest.getNewOrderNumber())
                .imageUrl(bannerRequest.getImage())
                .build();
    }
}
