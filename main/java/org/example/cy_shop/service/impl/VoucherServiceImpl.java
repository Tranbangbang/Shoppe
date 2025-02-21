package org.example.cy_shop.service.impl;

import org.example.cy_shop.configuration.jwtConfig.JwtProvider;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.voucher.VoucherRequest;
import org.example.cy_shop.dto.response.voucher.VoucherResponse;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.Shop;
import org.example.cy_shop.entity.Voucher;
import org.example.cy_shop.enums.EnumDiscountType;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.VoucherMapper;
import org.example.cy_shop.repository.IAccountRepository;
import org.example.cy_shop.repository.IShopRepository;
import org.example.cy_shop.repository.IVoucherRepository;
import org.example.cy_shop.service.IVoucherService;
import org.example.cy_shop.specification.VoucherSpecification;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VoucherServiceImpl implements IVoucherService {
    @Autowired
    private IVoucherRepository voucherRepository;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private IAccountRepository accountRepository;
    @Autowired
    private VoucherMapper voucherMapper;
    @Autowired
    private IShopRepository shopRepository;

    @Override
    public ApiResponse<List<VoucherResponse>> listVoucher() {
        try {
            String email = jwtProvider.getEmailContext();
            if (email == null || email.isEmpty()) {
                throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
            }
            Account account = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
            boolean isRoleShop = account.getUserRoles().stream()
                    .anyMatch(userRoleEntity -> userRoleEntity.getRole().getName().equals(Const.ROLE_SHOP));
            if (!isRoleShop) {
                throw new AppException(ErrorCode.FORBIDDEN_ROLE);
            }
            Shop shop = shopRepository.findByAccount(account)
                    .orElseThrow(() -> new AppException(ErrorCode.SHOP_NOT_FOUND));
            List<Voucher> vouchers = voucherRepository.findByShop(shop);
            vouchers.sort(Comparator.comparing(Voucher::getCreatedAt).reversed());
            List<VoucherResponse> voucherResponses = vouchers.stream()
                    .map(voucherMapper::toResponse)
                    .collect(Collectors.toList());
            return ApiResponse.<List<VoucherResponse>>builder()
                    .data(voucherResponses)
                    .message("List of vouchers for shop fetched successfully.")
                    .build();
        }catch (AppException e) {
            return ApiResponse.<List<VoucherResponse>>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<VoucherResponse>>builder()
                    .message("Failed to fetch vouchers: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<VoucherResponse> createVoucher(VoucherRequest voucherRequest) {
        try {
            if (voucherRequest.getDiscountValue() < 0) {
                throw new AppException(ErrorCode.Discount_value_cannot_be_negative);
            }
            if (voucherRequest.getMinOrderValue() < 0) {
                throw new AppException(ErrorCode.Minimum_order_value_cannot_be_negative);
            }
            if (voucherRequest.getMaxUsage() < 0) {
                throw new AppException(ErrorCode.Max_usage_cannot_be_negative);
            }
            String email = jwtProvider.getEmailContext();
            if (email == null || email.isEmpty()) {
                throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
            }
            Account account = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
            boolean isRoleSHop = account.getUserRoles().stream()
                    .anyMatch(userRoleEntity -> userRoleEntity.getRole().getName().equals(Const.ROLE_SHOP));
            if(!isRoleSHop){
                throw new AppException(ErrorCode.FORBIDDEN_ROLE);
            }
            Shop shop = shopRepository.findByAccount(account)
                    .orElseThrow(() -> new AppException(ErrorCode.SHOP_NOT_FOUND));
            Voucher voucher = voucherMapper.toEntity(voucherRequest);
            voucher.setShop(shop);
            voucher.setIsActive(true);
            voucher.setCreatedAt(LocalDateTime.now());
            voucher.setUpdatedAt(LocalDateTime.now());


            voucherRepository.save(voucher);

            List<Voucher> vouchers = voucherRepository.findAll();
            vouchers.sort(Comparator.comparing(Voucher::getCreatedAt).reversed());
            List<VoucherResponse> voucherResponses = vouchers.stream()
                    .map(voucherMapper::toResponse)
                    .toList();

            return ApiResponse.<VoucherResponse>builder()
                    .data(voucherResponses.get(0))
                    .message("Voucher created successfully")
                    .build();
        } catch (AppException e) {
            return ApiResponse.<VoucherResponse>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<VoucherResponse>builder()
                    .message("Failed to create voucher: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<VoucherResponse> updateVoucher(Long voucherId, VoucherRequest voucherRequest) {
        try {
            Voucher voucher = voucherRepository.findById(voucherId)
                    .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
            //voucher.setVoucherCode(voucherRequest.getVoucherCode());
            voucher.setDiscountType(EnumDiscountType.valueOf(voucherRequest.getDiscountType()));
            voucher.setDiscountValue(voucherRequest.getDiscountValue());
            voucher.setStartDate(voucherRequest.getStartDate().atStartOfDay());
            voucher.setEndDate(voucherRequest.getEndDate().atStartOfDay());
            voucher.setMaxUsage(voucherRequest.getMaxUsage());
            voucher.setUpdatedAt(LocalDateTime.now());
            voucher = voucherRepository.save(voucher);
            VoucherResponse voucherResponse = voucherMapper.toResponse(voucher);
            return ApiResponse.<VoucherResponse>builder()
                    .data(voucherResponse)
                    .message("Voucher updated successfully")
                    .build();
        } catch (AppException e) {
            return ApiResponse.<VoucherResponse>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<VoucherResponse>builder()
                    .message("Failed to update voucher: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<VoucherResponse> deleteVoucher(Long voucherId) {
        try{
            Voucher voucher = voucherRepository.findById(voucherId)
                    .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
            voucher.setMaxUsage(0);
            voucher = voucherRepository.save(voucher);
            VoucherResponse voucherResponse = voucherMapper.toResponse(voucher);
            return ApiResponse.<VoucherResponse>builder()
                    .data(voucherResponse)
                    .message("successfully")
                    .build();
        } catch (AppException e) {
            return ApiResponse.<VoucherResponse>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<VoucherResponse>builder()
                    .message("Failed to delete voucher: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<VoucherResponse>> searchVouchers(String voucherCode, String discountType,
                                                             LocalDate startDate, LocalDate endDate,
                                                             Boolean isActive) {
        String email = jwtProvider.getEmailContext();
        if (email == null || email.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Shop shop = shopRepository.findByEmail(email);
        LocalDateTime startDateTime = (startDate != null) ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = (endDate != null) ? endDate.atTime(23, 59, 59) : null;
        Specification<Voucher> specification = Specification
                .where(VoucherSpecification.hasVoucherCode(voucherCode))
                .and(VoucherSpecification.hasDiscountType(discountType))
                .and(VoucherSpecification.hasStartDate(startDateTime))
                .and(VoucherSpecification.hasEndDate(endDateTime))
                .and(VoucherSpecification.isActive(isActive))
                .and(VoucherSpecification.hasShopId(shop.getId()));

        List<Voucher> vouchers = voucherRepository.findAll(specification);
        vouchers.sort(Comparator.comparing(Voucher::getCreatedAt).reversed());
        List<VoucherResponse> voucherResponses = vouchers.stream()
                .map(voucherMapper::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.<List<VoucherResponse>>builder()
                .data(voucherResponses)
                .message("Voucher search result")
                .build();
    }


    @Override
    public ApiResponse<List<VoucherResponse>> listVoucherShop(Long id) {
        try {
            List<Voucher> vouchers = voucherRepository.findByShopId(id);
            vouchers.sort(Comparator.comparing(Voucher::getCreatedAt).reversed());
            List<VoucherResponse> voucherResponses = vouchers.stream()
                    .map(voucherMapper::toResponse)
                    .collect(Collectors.toList());
            return ApiResponse.<List<VoucherResponse>>builder()
                    .data(voucherResponses)
                    .message("List of vouchers for shop successfully.")
                    .build();
        } catch (AppException e) {
            return ApiResponse.<List<VoucherResponse>>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<VoucherResponse>>builder()
                    .message("Failed to fetch vouchers: " + e.getMessage())
                    .build();
        }
    }
}
