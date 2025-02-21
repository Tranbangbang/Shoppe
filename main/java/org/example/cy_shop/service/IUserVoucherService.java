package org.example.cy_shop.service;

import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.voucher.UserVoucherRequest;
import org.example.cy_shop.dto.response.voucher.UserVoucherResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserVoucherService {
    ApiResponse<String> saveVoucher(UserVoucherRequest request);
    ApiResponse<List<UserVoucherResponse>> listUserVouchers(Pageable pageable);
    ApiResponse<List<UserVoucherResponse>> getUserVouchersByShop(Long shopId);
}
