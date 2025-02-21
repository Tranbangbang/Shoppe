package org.example.cy_shop.service.impl;

import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.RevenueRequest;
import org.example.cy_shop.dto.response.DailyRevenueResponse;
import org.example.cy_shop.dto.response.DashboardStatisticsResponse;
import org.example.cy_shop.dto.response.MonthlyRevenueResponse;
import org.example.cy_shop.dto.response.TopShopResponse;
import org.example.cy_shop.dto.response.product.ProductStatisticsResponse;
import org.example.cy_shop.dto.response.shop.ShopPopularityResponse;
import org.example.cy_shop.dto.response.shop.ShopRevenueDTO;
import org.example.cy_shop.dto.response.shop.ShopStatisticReponse;
import org.example.cy_shop.enums.order.StatusOrderEnum;
import org.example.cy_shop.enums.order.StatusPaymentEnum;
import org.example.cy_shop.repository.IAccountRepository;
import org.example.cy_shop.repository.IShopRepository;
import org.example.cy_shop.repository.order.IOrderDetailRepository;
import org.example.cy_shop.repository.order.IOrderRepository;
import org.example.cy_shop.service.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService implements IAdminService {
    @Autowired
    private IOrderRepository orderRepository;
    @Autowired
    private IOrderDetailRepository orderDetailRepository;
    @Autowired
    private IShopRepository shopRepository;
    @Autowired
    private IAccountRepository accountRepository;

    @Override
    public ResponseEntity<?> getRevenueByPeriod(RevenueRequest revenueRequest) {
        try {
            LocalDateTime startDate = revenueRequest.getStartDate();
            LocalDateTime endDate = revenueRequest.getEndDate();
            List<ShopRevenueDTO> revenueDTOS = orderRepository.getShopRevenueByPeriod(
                    StatusOrderEnum.RECEIVED,
                    StatusPaymentEnum.PAID,
                    startDate,
                    endDate
            );
            return ResponseEntity.ok().body(
                    ApiResponse.<List<ShopRevenueDTO>>builder()
                            .message("Revenue data fetched successfully")
                            .data(revenueDTOS.stream().limit(10).collect(Collectors.toList()))
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<List<ShopRevenueDTO>>builder()
                            .code(101)
                            .message("Failed to fetch revenue data: " + e.getMessage())
                            .build()
            );
        }
    }


    @Override
    public ResponseEntity<?> getTotalRevenue(RevenueRequest revenueRequest) {
        try {
            Double totalRevenue = orderDetailRepository.calculateTotalRevenue(
                    StatusOrderEnum.RECEIVED,
                    StatusPaymentEnum.PAID
            );
            return ResponseEntity.ok().body(
                    ApiResponse.<Double>builder()
                            .message("Total revenue fetched successfully")
                            .data(totalRevenue)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<Double>builder()
                            .code(101)
                            .message("Failed to fetch total revenue: " + e.getMessage())
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<?> getDashboardStatistics() {
        try {
            Double totalRevenue = orderDetailRepository.calculateTotalRevenue(
                    StatusOrderEnum.RECEIVED,
                    StatusPaymentEnum.PAID
            );
            Long totalOrders = orderRepository.countTotalOrders(
                    StatusOrderEnum.RECEIVED
            );
            Long totalShops = shopRepository.countTotalShops();
            Long activeUsers = accountRepository.countTotalAccount();
            DashboardStatisticsResponse response = DashboardStatisticsResponse.builder()
                    .totalRevenue(totalRevenue)
                    .totalOrders(totalOrders)
                    .totalShops(totalShops)
                    .activeUsers(activeUsers)
                    .build();

            return ResponseEntity.ok().body(
                    ApiResponse.<DashboardStatisticsResponse>builder()
                            .message("Dashboard statistics fetched successfully")
                            .data(response)
                            .build()
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<DashboardStatisticsResponse>builder()
                            .code(101)
                            .message("Failed to fetch dashboard statistics: " + e.getMessage())
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<?> getTopShop() {
        try {
            List<TopShopResponse> topShops = orderRepository.getTopShopsToday(
                    StatusOrderEnum.RECEIVED,
                    StatusPaymentEnum.PAID,
                    PageRequest.of(0, 10)
            ).getContent();
            return ResponseEntity.ok().body(
                    ApiResponse.<List<TopShopResponse>>builder()
                            .message("Top shops fetched successfully for today")
                            .data(topShops)
                            .build()
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<List<TopShopResponse>>builder()
                            .code(101)
                            .message("Failed to fetch top shops for today: " + e.getMessage())
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<?> getTopShopsByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            List<TopShopResponse> topShops = orderRepository.getTopShopsByPeriod(
                    StatusOrderEnum.RECEIVED,
                    StatusPaymentEnum.PAID,
                    startDate,
                    endDate,
                    PageRequest.of(0, 10)
            ).getContent();
            return ResponseEntity.ok().body(
                    ApiResponse.<List<TopShopResponse>>builder()
                            .message("Top shops fetched successfully for the specified period")
                            .data(topShops)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<List<TopShopResponse>>builder()
                            .code(102)
                            .message("Failed to fetch top shops for the specified period: " + e.getMessage())
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<?> getMonthlyRevenue(int year) {
        try {
            List<Object[]> results = orderRepository.getMonthlyRevenue(
                    StatusOrderEnum.RECEIVED,
                    StatusPaymentEnum.PAID,
                    year
            );
            List<MonthlyRevenueResponse> monthlyRevenues = results.stream()
                    .map(result -> new MonthlyRevenueResponse(
                            (int) result[0],
                            (double) result[1]
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok().body(
                    ApiResponse.<List<MonthlyRevenueResponse>>builder()
                            .message("Monthly revenue fetched successfully")
                            .data(monthlyRevenues)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<List<MonthlyRevenueResponse>>builder()
                            .code(101)
                            .message("Failed to fetch monthly revenue: " + e.getMessage())
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<?> getDailyRevenue(RevenueRequest revenueRequest) {
        try {
            List<Object[]> results = orderRepository.getDailyRevenue(
                    StatusOrderEnum.RECEIVED,
                    StatusPaymentEnum.PAID,
                    revenueRequest.getStartDate(),
                    revenueRequest.getEndDate()
            );
            List<DailyRevenueResponse> dailyRevenues = results.stream()
                    .map(result -> new DailyRevenueResponse(
                            ((java.sql.Date) result[0]).toLocalDate(),
                            (double) result[1]
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok().body(
                    ApiResponse.<List<DailyRevenueResponse>>builder()
                            .message("Daily revenue fetched successfully")
                            .data(dailyRevenues)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<List<DailyRevenueResponse>>builder()
                            .code(101)
                            .message("Failed to fetch daily revenue: " + e.getMessage())
                            .build()
            );
        }
    }


    @Override
    public ResponseEntity<?> getShopPopularity() {
        try {
            LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.now();
            Long activeShops = shopRepository.countActiveShops(startDate, endDate);
            Long inactiveShops = shopRepository.countInactiveShops(startDate, endDate);
            Long newShops = shopRepository.countNewShops(startDate, endDate);
            ShopPopularityResponse response = new ShopPopularityResponse(activeShops, inactiveShops, newShops);
            return ResponseEntity.ok().body(
                    ApiResponse.<ShopPopularityResponse>builder()
                            .message("Shop popularity statistics fetched successfully")
                            .data(response)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<ShopPopularityResponse>builder()
                            .code(101)
                            .message("Failed to fetch shop popularity statistics: " + e.getMessage())
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<?> getShopStatistics() {
        try {
            LocalDateTime startDate = LocalDateTime.of(2023, 1, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.now();

            LocalDateTime today = LocalDate.now().atStartOfDay();
            LocalDateTime endDay = LocalDate.now().atTime(LocalTime.MAX);

            Long totalShops = shopRepository.countTotalShops();
            Long activeShops = shopRepository.countActiveShops(startDate, endDate);
            Long inactiveShops = shopRepository.countInactiveShops(startDate, endDate);
            Long newShops = shopRepository.countNewShops(today, endDay);
            ShopStatisticReponse response = new ShopStatisticReponse(totalShops, activeShops, inactiveShops, newShops);

            return ResponseEntity.ok().body(
                    ApiResponse.<ShopStatisticReponse>builder()
                            .message("Shop statistics fetched successfully")
                            .data(response)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<ShopStatisticReponse>builder()
                            .code(101)
                            .message("Failed to fetch shop statistics: " + e.getMessage())
                            .build()
            );
        }
    }


    @Override
    public ResponseEntity<?> getTopProducts() {
        try {
            List<ProductStatisticsResponse> topProducts = orderRepository.getTopProducts(
                    StatusOrderEnum.RECEIVED,
                    StatusPaymentEnum.PAID,
                    PageRequest.of(0, 5)
            );
            return ResponseEntity.ok().body(
                    ApiResponse.<List<ProductStatisticsResponse>>builder()
                            .message("Top products fetched successfully")
                            .data(topProducts)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<List<ProductStatisticsResponse>>builder()
                            .code(101)
                            .message("Failed to fetch top products: " + e.getMessage())
                            .build()
            );
        }
    }


}
