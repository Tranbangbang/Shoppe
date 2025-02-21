package org.example.cy_shop.mapper;

import org.example.cy_shop.dto.response.voucher.UserVoucherResponse;
import org.example.cy_shop.dto.response.voucher.VoucherResponse;
import org.example.cy_shop.entity.UserVoucher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserVoucherMapper {
    @Autowired
    VoucherMapper voucherMapper;
    public UserVoucherResponse toResponse(UserVoucher voucher){
        VoucherResponse voucherResponse = voucherMapper.toResponse(voucher.getVoucher());
        return UserVoucherResponse.builder()
                .id(voucher.getId())
                .usedAt(voucher.getUsedAt())
                .isValid(voucher.getIsValid())
                .voucher(voucherResponse)
                .build();
    }
}
