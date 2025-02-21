package org.example.cy_shop.mapper;

import org.example.cy_shop.dto.request.report.ReportRequest;
import org.example.cy_shop.dto.response.auth.AccountResponse;
import org.example.cy_shop.dto.response.report.ReportResponse;
import org.example.cy_shop.dto.response.product.ProductResponse;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.Report;
import org.example.cy_shop.entity.product.ProductEntity;
import org.example.cy_shop.enums.EnumReportStatus;
import org.example.cy_shop.mapper.product_mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReportMapper {
    @Autowired
    AccountMapper accountMapper;
    @Autowired
    ProductMapper productMapper;

    public ReportResponse toResponse(Report report){
        AccountResponse accountResponse = accountMapper.toResponse(report.getReporter());
        ProductResponse productResponse = productMapper.convertToResponse(report.getProduct());
        return ReportResponse.builder()
                .id(report.getId())
                .createdAt(report.getCreatedAt())
                .desc(report.getDesc())
                .shop_id(report.getProduct().getShop().getId())
                .reason(report.getReason())
                .status(report.getStatus())
                .reporter(accountResponse)
                .product(productResponse)
                .build();
    }
    public List<ReportResponse> toResponseDetail(List<Report> reports) {
        return reports.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    public Report toEntity(ReportRequest reportRequest, Account reporter, ProductEntity product) {
        return Report.builder()
                .reporter(reporter)
                .product(product)
                .reason(reportRequest.getReason())
                .desc(reportRequest.getReason())
                .shop_id(product.getShop().getId())
                .createdAt(LocalDateTime.now())
                .status(EnumReportStatus.PENDING)
                .build();
    }
}
