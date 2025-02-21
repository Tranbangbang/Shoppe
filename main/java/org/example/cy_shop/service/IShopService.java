package org.example.cy_shop.service;

import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.shop.ShopRequest;
import org.example.cy_shop.dto.response.auth.AccountResponse;
import org.example.cy_shop.dto.response.product.ProductShopStatisticsResponse;
import org.example.cy_shop.dto.response.shop.ShopDetailResponse;
import org.example.cy_shop.dto.response.shop.ShopResponse;
import org.example.cy_shop.dto.response.shop.ShopRevenueResponse;
import org.example.cy_shop.entity.Shop;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface IShopService {
    ShopResponse findByEmail(String email);
    ShopResponse findByUserName(String userName);
    ApiResponse<ShopResponse> createShop(ShopRequest shopRequest);
    //ApiResponse<List<ShopDetailResponse>> listShop(Pageable pageable);

    ApiResponse<Map<String, Object>> listShop(Pageable pageable, String shopName);

    ApiResponse<Shop> findMyShop();
    ApiResponse<String> deleteShopByNotActive();
    List<ProductShopStatisticsResponse> getTopSellingProductsForShop(LocalDateTime startDate, LocalDateTime endDate);
    List<ProductShopStatisticsResponse> getTopSellingProductsToday();
    ShopRevenueResponse getTodayShopRevenue(LocalDateTime startOfDay, LocalDateTime endOfDay);
    ApiResponse<AccountResponse> getAccountByShopName(String shopName);

    ApiResponse<ShopDetailResponse> getShopProfile();
    ApiResponse<ShopDetailResponse> updateShopProfile(ShopRequest shopRequest);

}
