package org.example.cy_shop.service;

import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.banner.BannerRequest;
import org.example.cy_shop.dto.request.banner.UpdateBannerRequest;
import org.example.cy_shop.dto.response.banner.BannerResponse;
import org.example.cy_shop.entity.Banner;

import java.util.List;

public interface IBannerService {
    ApiResponse<String> createBanner(BannerRequest bannerRequest);
    ApiResponse<List<Banner>> getAllBanners();
    ApiResponse<BannerResponse> getBannerById(Long id);
    ApiResponse<String> updateBanner(Long id, UpdateBannerRequest updateBannerRequest);
    ApiResponse<String> deleteBanner(Long id);
}
