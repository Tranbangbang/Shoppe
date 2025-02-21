package org.example.cy_shop.controller.Report;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.report.ReportRequest;
import org.example.cy_shop.dto.response.report.ReportResponse;
import org.example.cy_shop.dto.response.shop.ShopResponse;
import org.example.cy_shop.enums.EnumReportStatus;
import org.example.cy_shop.service.IReportService;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "06. Report")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/report")
public class ReportController {
    @Autowired
    IReportService reportService;
    @PostMapping("/create")
    public ApiResponse<String> createReport(@RequestBody ReportRequest reportRequest) {
        return reportService.createReport(reportRequest);
    }

    @PutMapping("/{reportId}/status")
    public ApiResponse<String> updateReportStatus(@PathVariable Long reportId, @RequestParam EnumReportStatus newStatus) {
        return reportService.updateReportStatus(reportId, newStatus);
    }

    @GetMapping
    public ApiResponse<List<ReportResponse>> getAllReports(
    ) {
        return reportService.getAllReport();
    }

    @GetMapping("/{id}")
    public ApiResponse<ReportResponse> getReportById( Long id
    ) {
        return reportService.getReportById(id);
    }


    @GetMapping("/list-shop-banned")
    public ApiResponse<List<ShopResponse>> getShop(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return reportService.getShopBanned(pageable);
    }

}
