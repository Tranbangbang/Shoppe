package org.example.cy_shop.specification;

import org.example.cy_shop.entity.Voucher;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class VoucherSpecification {
    public static Specification<Voucher> hasVoucherCode(String voucherCode) {
        return (root, query, criteriaBuilder) ->
                voucherCode == null ? null : criteriaBuilder.like(root.get("voucherCode"), "%" + voucherCode + "%");
    }

    public static Specification<Voucher> hasDiscountType(String discountType) {
        return (root, query, criteriaBuilder) ->
                discountType == null ? null : criteriaBuilder.equal(root.get("discountType"), discountType);
    }

    public static Specification<Voucher> hasStartDate(LocalDateTime startDate) {
        return (root, query, criteriaBuilder) ->
                startDate == null ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), startDate);
    }

    public static Specification<Voucher> hasEndDate(LocalDateTime endDate) {
        return (root, query, criteriaBuilder) ->
                endDate == null ? null : criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), endDate);
    }

    public static Specification<Voucher> isActive(Boolean isActive) {
        return (root, query, criteriaBuilder) ->
                isActive == null ? null : criteriaBuilder.equal(root.get("isActive"), isActive);
    }
    public static Specification<Voucher> hasShopId(Long shopId) {
        return (root, query, criteriaBuilder) ->
                shopId != null ? criteriaBuilder.equal(root.get("shop").get("id"), shopId) : null;
    }

}
