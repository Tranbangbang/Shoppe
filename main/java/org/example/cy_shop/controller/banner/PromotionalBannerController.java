package org.example.cy_shop.controller.banner;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.banner.PromotionalBannerRequest;
import org.example.cy_shop.dto.request.banner.UpdatePromotionalBannerRequest;
import org.example.cy_shop.dto.response.banner.PromotionalBannerResponse;
import org.example.cy_shop.service.IPromotionalBannerService;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "10. PromotionalBanner")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/promotional-banner")
public class PromotionalBannerController {
    @Autowired
    private IPromotionalBannerService promotionalBannerService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createBanner(@ModelAttribute PromotionalBannerRequest bannerRequest) {
        ApiResponse<String> response = promotionalBannerService.createBanner(bannerRequest);
        if (response.getCode() != 200) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<PromotionalBannerResponse>>> getAllBanners() {
        ApiResponse<List<PromotionalBannerResponse>> response = promotionalBannerService.getAllBanners();
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PromotionalBannerResponse>> getBannerById(@PathVariable Long id) {
        ApiResponse<PromotionalBannerResponse> response = promotionalBannerService.getBannerById(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateBanner(
            @PathVariable Long id,
            @RequestBody UpdatePromotionalBannerRequest updateBannerRequest) {
        ApiResponse<String> response = promotionalBannerService.updateBanner(id, updateBannerRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteBanner(@PathVariable Long id) {
        ApiResponse<String> response = promotionalBannerService.deleteBanner(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
