package org.example.cy_shop.service;

import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.banner.PromotionalBannerRequest;
import org.example.cy_shop.dto.request.banner.UpdatePromotionalBannerRequest;
import org.example.cy_shop.dto.response.banner.PromotionalBannerResponse;

import java.util.List;

public interface IPromotionalBannerService {
    ApiResponse<String> createBanner(PromotionalBannerRequest bannerRequest);
    ApiResponse<List<PromotionalBannerResponse>> getAllBanners();
    ApiResponse<PromotionalBannerResponse> getBannerById(Long id);
    ApiResponse<String> updateBanner(Long id, UpdatePromotionalBannerRequest updateBannerRequest);
    ApiResponse<String> deleteBanner(Long id);
}
