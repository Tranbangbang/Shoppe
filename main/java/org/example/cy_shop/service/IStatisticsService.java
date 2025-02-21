package org.example.cy_shop.service;

import java.time.LocalDateTime;
import java.util.Map;

public interface IStatisticsService {
    Map<String, Object> calculateStatisticsForShop(LocalDateTime startDate, LocalDateTime endDate);
    Map<String, Object> calculateTodayStatisticsForShop();
}
