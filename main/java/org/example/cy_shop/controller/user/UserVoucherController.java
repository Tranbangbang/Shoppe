package org.example.cy_shop.controller.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.voucher.UserVoucherRequest;
import org.example.cy_shop.dto.response.voucher.UserVoucherResponse;
import org.example.cy_shop.service.IUserVoucherService;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "07. UserVoucher")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/uservoucher")
public class UserVoucherController {
    @Autowired
    private IUserVoucherService userVoucherService;

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<String>> saveVoucher(@RequestBody UserVoucherRequest request) {
        ApiResponse<String> response = userVoucherService.saveVoucher(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list-voucher")
    public ResponseEntity<ApiResponse<List<UserVoucherResponse>>> getListVoucher(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        ApiResponse<List<UserVoucherResponse>> response = userVoucherService.listUserVouchers(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<ApiResponse<List<UserVoucherResponse>>> getUserVouchersByShop(
            @PathVariable Long shopId
    ) {
        ApiResponse<List<UserVoucherResponse>> response = userVoucherService.getUserVouchersByShop(shopId);
        if (response.getCode() != 200) {
            return ResponseEntity.status(400).body(response);
        }
        return ResponseEntity.ok(response);
    }
}
