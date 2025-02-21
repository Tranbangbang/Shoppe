package org.example.cy_shop.mapper;

import org.example.cy_shop.dto.request.banner.BannerRequest;
import org.example.cy_shop.dto.request.banner.PromotionalBannerRequest;
import org.example.cy_shop.dto.request.banner.UpdateBannerRequest;
import org.example.cy_shop.dto.request.banner.UpdatePromotionalBannerRequest;
import org.example.cy_shop.dto.response.banner.BannerResponse;
import org.example.cy_shop.dto.response.banner.PromotionalBannerResponse;
import org.example.cy_shop.entity.Banner;
import org.example.cy_shop.entity.PromotionalBanner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PromotionalBannerMapper {
    public PromotionalBannerResponse toResponse(PromotionalBanner banner){
        return PromotionalBannerResponse.builder()
                .id(banner.getId())
                .name(banner.getName())
                .image(banner.getImageUrl())
                //.createdAt(banner.getCreatedAt())
                .build();
    }

    public List<PromotionalBannerResponse> toResponseList(List<PromotionalBanner> banners) {
        return banners.stream()
                .map(this::toResponse)
                .toList();
    }
    public PromotionalBanner toEntity(PromotionalBannerRequest bannerRequest){
        return PromotionalBanner.builder()
                .id(bannerRequest.getId())
                .name(bannerRequest.getName())
                .imageUrl(String.valueOf(bannerRequest.getImage()))
                .build();
    }

    public PromotionalBanner toEntity(UpdatePromotionalBannerRequest bannerRequest){
        return PromotionalBanner.builder()
                .id(bannerRequest.getId())
                .name(bannerRequest.getName())
                .imageUrl(bannerRequest.getImage())
                .build();
    }
}
