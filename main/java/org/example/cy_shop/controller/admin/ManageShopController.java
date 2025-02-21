package org.example.cy_shop.controller.admin;


import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cy_shop.controller.order.client.ClientOrderController;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.RevenueRequest;
import org.example.cy_shop.dto.response.product.ProductStatisticsResponse;
import org.example.cy_shop.dto.response.shop.ShopPopularityResponse;
import org.example.cy_shop.dto.response.shop.ShopRevenueDTO;
import org.example.cy_shop.service.IAdminService;
import org.example.cy_shop.service.impl.SearchServiceImpl;
import org.example.cy_shop.service.impl.order.OrderService;
import org.example.cy_shop.service.impl.product.ProductService;
import org.example.cy_shop.service.order.IOrderService;
import org.example.cy_shop.specification.ProductSpecification;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "10. MANAGER_SHOP")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/manager_shop")
public class ManageShopController {
    @Autowired
    private IOrderService orderService;
    @Autowired
    private IAdminService adminService;
    

    @GetMapping("/topProducts")
    public ResponseEntity<?> getTopProducts() {
        return adminService.getTopProducts();
    }

    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics() {
        return adminService.getDashboardStatistics();
    }

    @GetMapping("/topShops/today")
    public ResponseEntity<?> getTopShopsToday() {
        return adminService.getTopShop();
    }

    @PostMapping("/topShops/byPeriod")
    public ResponseEntity<?> getTopShopsByPeriod(@RequestBody RevenueRequest revenueRequest) {
        return adminService.getTopShopsByPeriod(
                revenueRequest.getStartDate(),
                revenueRequest.getEndDate()
        );
    }

    @GetMapping("/monthlyRevenue")
    public ResponseEntity<?> getMonthlyRevenue(@RequestParam int year) {
        return adminService.getMonthlyRevenue(year);
    }


    @PostMapping("/dailyRevenue")
    public ResponseEntity<?> getDailyRevenue(@RequestBody RevenueRequest request
        ) {
        return adminService.getDailyRevenue(request);
    }

    @GetMapping("/shopPopularity")
    public ResponseEntity<?> getShopPopularity() {
        return adminService.getShopPopularity();
    }

    @GetMapping("/shopStatistics")
    public ResponseEntity<?> getShopStatistics() {
        return adminService.getShopStatistics();
    }




    /*
    @PostMapping("/getTotalRevenue")
    public ResponseEntity<?> getTotalRevenue(@RequestBody RevenueRequest revenueRequest) {
        return adminService.getTotalRevenue(revenueRequest);
    }
     */
}

//SearchServiceImpl
//OrderService
//ClientOrderController
//ProductService