package org.example.cy_shop.mapper;

import org.example.cy_shop.dto.request.voucher.VoucherRequest;
import org.example.cy_shop.dto.response.voucher.VoucherResponse;
import org.example.cy_shop.entity.Voucher;
import org.example.cy_shop.enums.EnumDiscountType;
import org.springframework.stereotype.Component;

@Component
public class VoucherMapper {
    public VoucherResponse toResponse(Voucher voucher){
        return VoucherResponse.builder()
                .id(voucher.getId())
                .discountType(String.valueOf(voucher.getDiscountType()))
                .voucherCode(voucher.getVoucherCode())
                .discountValue(voucher.getDiscountValue())
                .startDate(voucher.getStartDate())
                .endDate(voucher.getEndDate())
                .isActive(voucher.getIsActive())
                .maxUsage(voucher.getMaxUsage())
                .createAt(voucher.getCreatedAt())
                .updatedAt(voucher.getUpdatedAt())
                .minOrderValue(voucher.getMinOrderValue())
                .build();
    }

    public Voucher toEntity(VoucherRequest voucherRequest){
        return Voucher.builder()
                .voucherCode(Voucher.generateRandomCode())
                .discountType(EnumDiscountType.valueOf(voucherRequest.getDiscountType()))
                .discountValue(voucherRequest.getDiscountValue())
                .startDate(voucherRequest.getStartDate().atStartOfDay())
                .endDate(voucherRequest.getEndDate().atStartOfDay())
                .maxUsage(voucherRequest.getMaxUsage())
                .minOrderValue(voucherRequest.getMinOrderValue())
                .build();
    }
}
