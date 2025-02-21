package org.example.cy_shop.service;

import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.report.ReportRequest;
import org.example.cy_shop.dto.response.report.ReportResponse;
import org.example.cy_shop.dto.response.shop.ShopResponse;
import org.example.cy_shop.dto.response.product.ProductResponse;
import org.example.cy_shop.enums.EnumReportStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IReportService {
    ApiResponse<String> createReport(ReportRequest reportRequest);
    ApiResponse<String> updateReportStatus(Long reportId, EnumReportStatus newStatus);
    ApiResponse<List<ReportResponse>> getAllReport();
    ApiResponse<ReportResponse> getReportById(Long id);
    ApiResponse<List<ShopResponse>> getShopBanned(Pageable pageable);
    ApiResponse<List<ProductResponse>> getViolatedProductsByShop(Long shopId,Pageable pageable);
}
