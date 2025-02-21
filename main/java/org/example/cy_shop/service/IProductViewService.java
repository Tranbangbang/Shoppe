package org.example.cy_shop.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.cy_shop.dto.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface IProductViewService {
    ResponseEntity<ApiResponse<Long>> recordProductView(Long productId, HttpServletRequest request);
    Long recordView(Long productId, String userIp);
}
