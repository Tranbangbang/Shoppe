package org.example.cy_shop.service;

import org.example.cy_shop.dto.request.RevenueRequest;
import org.example.cy_shop.dto.response.product.ProductStatisticsResponse;
import org.example.cy_shop.dto.response.shop.ShopPopularityResponse;
import org.example.cy_shop.dto.response.shop.ShopRevenueDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public interface IAdminService {
    ResponseEntity<?> getRevenueByPeriod(RevenueRequest revenueRequest);
    ResponseEntity<?> getTotalRevenue(RevenueRequest revenueRequest);


    ResponseEntity<?> getDashboardStatistics();
    ResponseEntity<?> getTopShop();
    ResponseEntity<?> getTopShopsByPeriod(LocalDateTime startDate, LocalDateTime endDate);
    ResponseEntity<?> getMonthlyRevenue(int year);
    ResponseEntity<?> getDailyRevenue(RevenueRequest revenueRequest);
    ResponseEntity<?> getTopProducts();
    ResponseEntity<?> getShopPopularity();
    ResponseEntity<?> getShopStatistics();
}
