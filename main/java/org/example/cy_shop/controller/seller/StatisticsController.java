package org.example.cy_shop.controller.seller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cy_shop.service.IStatisticsService;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@Tag(name = "SELLER_04. MANAGER_PRODUCT(SELLER)")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/statistic")
public class StatisticsController {
    @Autowired
    private IStatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<?> getStatisticsForShop(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        Map<String, Object> stats = statisticsService.calculateStatisticsForShop(startDate, endDate);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/today")
    public ResponseEntity<?> getTodayStatisticsForShop() {
        Map<String, Object> stats = statisticsService.calculateTodayStatisticsForShop();
        return ResponseEntity.ok(stats);
    }

}
