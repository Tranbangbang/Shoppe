package org.example.cy_shop.service.impl;

import org.example.cy_shop.configuration.jwtConfig.JwtProvider;
import org.example.cy_shop.entity.Shop;
import org.example.cy_shop.enums.order.StatusOrderEnum;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.repository.IShopRepository;
import org.example.cy_shop.repository.order.IOrderRepository;
import org.example.cy_shop.repository.product_repository.IProductViewRepository;
import org.example.cy_shop.service.IStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements IStatisticsService {
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    private IShopRepository shopRepository;
    @Autowired
    private IProductViewRepository productViewRepository;
    @Autowired
    private IOrderRepository orderRepository;
    @Override
    public Map<String, Object> calculateStatisticsForShop(LocalDateTime startDate, LocalDateTime endDate) {
        String email = jwtProvider.getEmailContext();
        Shop shop = shopRepository.findByEmail(email);
        if (shop == null) {
            throw new AppException(ErrorCode.SHOP_NOT_FOUND);
        }

        Map<String, Object> stats = new HashMap<>();
        Long views = productViewRepository.countViewsByShop(shop.getId(), startDate, endDate);
        stats.put("views", views);
        Long confirmedOrders = orderRepository.countConfirmedOrdersByShop(shop.getId(), StatusOrderEnum.RECEIVED, startDate, endDate);
        stats.put("confirmedOrders", confirmedOrders);
        List<Map<String, Object>> revenueDetails = new ArrayList<>();
        LocalDate currentDate = startDate.toLocalDate();
        LocalDate endDt = endDate.toLocalDate();

        while (!currentDate.isAfter(endDt)) {
            LocalDateTime startOfDay = currentDate.atStartOfDay();
            LocalDateTime endOfDay = currentDate.atTime(23, 59, 59);
            Double dailyRevenue = orderRepository.calculateRevenueByShop(shop.getId(), StatusOrderEnum.RECEIVED, startOfDay, endOfDay);
            Map<String, Object> dailyData = new HashMap<>();
            dailyData.put("date", currentDate.toString());
            dailyData.put("revenue", dailyRevenue != null ? dailyRevenue : 0.0);
            revenueDetails.add(dailyData);
            currentDate = currentDate.plusDays(1);
        }
        stats.put("revenueDetails", revenueDetails);
        Double totalRevenue = revenueDetails.stream()
                .mapToDouble(day -> (Double) day.get("revenue"))
                .sum();
        stats.put("totalRevenue", totalRevenue);
        Double conversionRate = (views != 0) ? (double) confirmedOrders / views * 100 : 0.0;
        stats.put("conversionRate", conversionRate);
        return stats;
    }

    @Override
    public Map<String, Object> calculateTodayStatisticsForShop() {
        String email = jwtProvider.getEmailContext();
        Shop shop = shopRepository.findByEmail(email);
        if (shop == null) {
            throw new AppException(ErrorCode.SHOP_NOT_FOUND);
        }

        Map<String, Object> stats = new HashMap<>();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.now();
        Long views = productViewRepository.countViewsByShop(shop.getId(), startOfDay, endOfDay);
        stats.put("views", views);
        Long confirmedOrders = orderRepository.countConfirmedOrdersByShop(shop.getId(), StatusOrderEnum.RECEIVED, startOfDay, endOfDay);
        stats.put("confirmedOrders", confirmedOrders);
        Double dailyRevenue = orderRepository.calculateRevenueByShop(shop.getId(), StatusOrderEnum.RECEIVED, startOfDay, endOfDay);
        stats.put("revenue", dailyRevenue != null ? dailyRevenue : 0.0);
        Double conversionRate = (views != 0) ? (double) confirmedOrders / views * 100 : 0.0;
        stats.put("conversionRate", conversionRate);

        return stats;
    }


}
