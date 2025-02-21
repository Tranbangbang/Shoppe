package org.example.cy_shop.controller.banner;


import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.banner.BannerRequest;
import org.example.cy_shop.dto.request.banner.UpdateBannerRequest;
import org.example.cy_shop.dto.response.banner.BannerResponse;
import org.example.cy_shop.entity.Banner;
import org.example.cy_shop.service.IBannerService;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "09. BANNER")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/banner")
public class BannerController {
    @Autowired
    private IBannerService bannerService;

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createBanner(@ModelAttribute BannerRequest bannerRequest) {
        ApiResponse<String> response = bannerService.createBanner(bannerRequest);
        if (response.getCode() != 200) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Banner>>> getAllBanners() {
        ApiResponse<List<Banner>> response = bannerService.getAllBanners();
        return ResponseEntity.status(response.getCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BannerResponse>> getAllBanners(@PathVariable Long id) {
        ApiResponse<BannerResponse> response = bannerService.getBannerById(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> updateBanner(
            @PathVariable Long id,
            @RequestBody UpdateBannerRequest updateBannerRequest) {
        ApiResponse<String> response = bannerService.updateBanner(id, updateBannerRequest);
        return ResponseEntity.status(response.getCode()).body(response);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteBanner(@PathVariable Long id) {
        ApiResponse<String> response = bannerService.deleteBanner(id);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}
