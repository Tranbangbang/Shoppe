package org.example.cy_shop.controller.Shop;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.RevenueRequest;
import org.example.cy_shop.dto.request.shop.ShopRequest;
import org.example.cy_shop.dto.response.auth.AccountResponse;
import org.example.cy_shop.dto.response.product.ProductResponse;
import org.example.cy_shop.dto.response.product.ProductShopStatisticsResponse;
import org.example.cy_shop.dto.response.shop.ShopDetailResponse;
import org.example.cy_shop.dto.response.shop.ShopResponse;
import org.example.cy_shop.dto.response.shop.ShopRevenueResponse;
import org.example.cy_shop.service.IReportService;
import org.example.cy_shop.service.IShopService;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Tag(name = "04. Shop")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/shop")
public class ShopController {
    @Autowired
    private IShopService shopService;
    @Autowired
    private IReportService reportService;
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ShopResponse>> createShop(@RequestBody ShopRequest shopRequest) {
        ApiResponse<ShopResponse> response = shopService.createShop(shopRequest);
        if (response.getCode() != 200) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/delete")
    public ApiResponse<String> deleteShop() {
        return shopService.deleteShopByNotActive();
    }

    @GetMapping("/list-shop")
    public ApiResponse<Map<String, Object>> viewShop(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "5") int size,
                                                     @RequestParam(required = false) String shopName) {
        Pageable pageable = PageRequest.of(page, size);
        return shopService.listShop(pageable, shopName);
    }



    @GetMapping("/violated/{shopId}")
    public ApiResponse<List<ProductResponse>> getViolatedProductsByShop(@PathVariable Long shopId,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "2") int size)
    {
        Pageable pageable = PageRequest.of(page, size);
        return reportService.getViolatedProductsByShop(shopId,pageable);
    }

    @PostMapping("/getTopSellingProducts")
    public ApiResponse<List<ProductShopStatisticsResponse>> getTopSellingProducts(@RequestBody RevenueRequest revenueRequest) {
        try {
            List<ProductShopStatisticsResponse> topSellingProducts = shopService.getTopSellingProductsForShop(
                    revenueRequest.getStartDate(), revenueRequest.getEndDate());
            return ApiResponse.<List<ProductShopStatisticsResponse>>builder()
                    .message("Top 5 selling products fetched successfully")
                    .data(topSellingProducts)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<ProductShopStatisticsResponse>>builder()
                    .code(101)
                    .message("Failed to fetch top selling products: " + e.getMessage())
                    .build();
        }
    }

    @PostMapping("/getTopSellingProductsToday")
    public ApiResponse<List<ProductShopStatisticsResponse>> getTopSellingProductsToday() {
        try {
            List<ProductShopStatisticsResponse> topSellingProducts = shopService.getTopSellingProductsToday();
            return ApiResponse.<List<ProductShopStatisticsResponse>>builder()
                    .message("Top 5 selling products fetched successfully for today")
                    .data(topSellingProducts)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<ProductShopStatisticsResponse>>builder()
                    .code(101)
                    .message("Failed to fetch top selling products for today: " + e.getMessage())
                    .build();
        }
    }


    @GetMapping("/revenue/today")
    public ResponseEntity<ShopRevenueResponse> getTodayShopRevenue() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        ShopRevenueResponse response = shopService.getTodayShopRevenue(startOfDay, endOfDay);
        if (response == null) {
            response = new ShopRevenueResponse(0.0, 0L, 0L);
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/account-by-shopname")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountByShopName(@RequestParam String shopName) {
        ApiResponse<AccountResponse> response = shopService.getAccountByShopName(shopName);
        return ResponseEntity.status(response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<ShopDetailResponse>> getShopProfile() {
        ApiResponse<ShopDetailResponse> response = shopService.getShopProfile();
        return ResponseEntity.status(response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<ShopDetailResponse>> updateShopProfile(@RequestBody ShopRequest shopRequest) {
        ApiResponse<ShopDetailResponse> response = shopService.updateShopProfile(shopRequest);
        return ResponseEntity.status(response.getCode() == 200 ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(response);
    }


}
