package org.example.cy_shop.controller.Shop;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.voucher.VoucherRequest;
import org.example.cy_shop.dto.response.shop.ShopResponse;
import org.example.cy_shop.dto.response.voucher.VoucherResponse;
import org.example.cy_shop.service.IVoucherService;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "04. Shop")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/voucher")
public class VoucherController {
    @Autowired
    private IVoucherService voucherService;

    @GetMapping("/list")
    public ApiResponse<List<VoucherResponse>> listVoucher() {
        return voucherService.listVoucher();
    }

    @GetMapping("/{shopId}")
    public ApiResponse<List<VoucherResponse>> listVoucherShopById(@PathVariable Long shopId) {
        return voucherService.listVoucherShop(shopId);
    }
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<VoucherResponse>> createVoucher(@RequestBody VoucherRequest voucherRequest) {

        ApiResponse<VoucherResponse> response = voucherService.createVoucher(voucherRequest);
        if (response.getCode() != 200) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }
    @PutMapping("/update/{voucherId}")
    public ResponseEntity<ApiResponse<VoucherResponse>> updateVoucher(@PathVariable Long voucherId, @RequestBody VoucherRequest voucherRequest) {
        ApiResponse<VoucherResponse> response= voucherService.updateVoucher(voucherId, voucherRequest);
        if (response.getCode() != 200) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/delete/{voucherId}")
    public ApiResponse<VoucherResponse> deleteVoucher(@PathVariable Long voucherId) {
        return voucherService.deleteVoucher(voucherId);
    }
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<VoucherResponse>>> searchVouchers(
            @RequestParam(required = false) String voucherCode,
            @RequestParam(required = false) String discountType,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) Boolean isActive) {

        ApiResponse<List<VoucherResponse>> response = voucherService.searchVouchers(voucherCode, discountType, startDate, endDate, isActive);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
