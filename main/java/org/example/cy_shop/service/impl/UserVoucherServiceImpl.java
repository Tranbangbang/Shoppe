package org.example.cy_shop.service.impl;

import org.example.cy_shop.configuration.jwtConfig.JwtProvider;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.voucher.UserVoucherRequest;
import org.example.cy_shop.dto.response.voucher.UserVoucherResponse;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.UserVoucher;
import org.example.cy_shop.entity.Voucher;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.UserVoucherMapper;
import org.example.cy_shop.repository.IAccountRepository;
import org.example.cy_shop.repository.IUserVoucherRepository;
import org.example.cy_shop.repository.IVoucherRepository;
import org.example.cy_shop.service.IUserVoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserVoucherServiceImpl implements IUserVoucherService {
    @Autowired
    private IUserVoucherRepository userVoucherRepository;
    @Autowired
    private IAccountRepository accountRepository;
    @Autowired
    private IVoucherRepository voucherRepository;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private UserVoucherMapper userVoucherMapper;
    @Override
    public ApiResponse<String> saveVoucher(UserVoucherRequest request) {
        String email = jwtProvider.getEmailContext();
        if (email == null || email.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        Voucher voucher = voucherRepository.findById(request.getVoucherId())
                .orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
        if (!voucher.getIsActive()) {
            throw new AppException(ErrorCode.VOUCHER_INACTIVE);
        }
        if (voucher.getMaxUsage() <= 0) {
            throw new AppException(ErrorCode.VOUCHER_EXHAUSTED);
        }
        if (voucher.getStartDate().isAfter(LocalDateTime.now())) {
            throw new AppException(ErrorCode.VOUCHER_NOT_STARTED_YET);
        }
        if (voucher.getEndDate().isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.VOUCHER_EXPIRED);
        }
        Optional<UserVoucher> existingUserVoucher = userVoucherRepository.findByAccountAndVoucher(account, voucher);
        if (existingUserVoucher.isPresent()) {
            UserVoucher userVoucher = existingUserVoucher.get();
            if (userVoucher.getIsValid()) {
                throw new AppException(ErrorCode.VOUCHER_ALREADY_USED);
            } else {
                userVoucher.setIsValid(true);
                userVoucher.setUsedAt(LocalDateTime.now());
                userVoucherRepository.save(userVoucher);

                voucher.setMaxUsage(voucher.getMaxUsage() - 1);
                voucherRepository.save(voucher);

                return ApiResponse.<String>builder()
                        .message("Voucher status updated successfully.")
                        .build();
            }
        }
        UserVoucher userVoucher = new UserVoucher();
        userVoucher.setAccount(account);
        userVoucher.setVoucher(voucher);
        userVoucher.setIsValid(true);
        userVoucher.setUsedAt(LocalDateTime.now());
        userVoucherRepository.save(userVoucher);
        voucher.setMaxUsage(voucher.getMaxUsage() - 1);
        voucherRepository.save(voucher);
        return ApiResponse.<String>builder()
                .message("Save voucher successfully")
                .build();
    }

    @Override
    public ApiResponse<List<UserVoucherResponse>> listUserVouchers(Pageable pageable) {
        String email = jwtProvider.getEmailContext();
        if (email == null || email.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        List<UserVoucher> userVouchers = userVoucherRepository.findByAccount(account,pageable);
        List<UserVoucherResponse> responseList = userVouchers.stream()
                .map(userVoucherMapper::toResponse)
                .collect(Collectors.toList());
        return ApiResponse.<List<UserVoucherResponse>>builder()
                .data(responseList)
                .message("User vouchers fetched successfully")
                .build();
    }

    @Override
    public ApiResponse<List<UserVoucherResponse>> getUserVouchersByShop(Long shopId) {
        try{
            String email = jwtProvider.getEmailContext();
            if (email == null || email.isEmpty()) {
                throw new AppException(ErrorCode.USER_NOT_FOUND);
            }
            Account account = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
            List<UserVoucher> userVouchers = userVoucherRepository.findUserVouchersByAccountIdAndShopId(account.getId(),shopId);
            List<UserVoucherResponse> responseList = userVouchers.stream()
                    .map(userVoucherMapper::toResponse)
                    .toList();
            return ApiResponse.<List<UserVoucherResponse>>builder()
                    .data(responseList)
                    .message("User vouchers fetched successfully")
                    .build();
        }catch (AppException e) {
            return ApiResponse.<List<UserVoucherResponse>>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<UserVoucherResponse>>builder()
                    .message("Failed: " + e.getMessage())
                    .build();
        }

    }


}
