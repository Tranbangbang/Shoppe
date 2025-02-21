package org.example.cy_shop.service;

import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.voucher.VoucherRequest;
import org.example.cy_shop.dto.response.voucher.VoucherResponse;

import java.time.LocalDate;
import java.util.List;

public interface IVoucherService {
    ApiResponse<List<VoucherResponse>> listVoucher();
    ApiResponse<VoucherResponse> createVoucher(VoucherRequest voucherRequest);
    ApiResponse<VoucherResponse> updateVoucher(Long voucherId, VoucherRequest voucherRequest);
    ApiResponse<VoucherResponse> deleteVoucher(Long voucherId);
    ApiResponse<List<VoucherResponse>> searchVouchers(String voucherCode, String discountType,
                                                      LocalDate startDate, LocalDate endDate,
                                                      Boolean isActive);
    ApiResponse<List<VoucherResponse>> listVoucherShop(Long id);
}