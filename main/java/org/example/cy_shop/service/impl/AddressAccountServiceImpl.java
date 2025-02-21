package org.example.cy_shop.service.impl;

import org.example.cy_shop.configuration.jwtConfig.JwtProvider;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.address.AddressAccountRequest;
import org.example.cy_shop.dto.response.address.AddressAccountResponse;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.AddressAccount;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.AddressAccountMapper;
import org.example.cy_shop.repository.IAccountRepository;
import org.example.cy_shop.repository.IAddressAccountRepository;
import org.example.cy_shop.service.IAddressAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AddressAccountServiceImpl implements IAddressAccountService {
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private IAccountRepository accountRepository;
    @Autowired
    private AddressAccountMapper addressAccountMapper;
    @Autowired
    private IAddressAccountRepository addressAccountRepository;
    @Override
    public ApiResponse<String> createAddress(AddressAccountRequest addressAccountRequest) {
        try{
            String email = jwtProvider.getEmailContext();
            if (email == null || email.isEmpty()) {
                throw new AppException(ErrorCode.USER_NOT_FOUND);
            }
            Account account = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
            /*
            if (addressAccountRequest.getFullName().matches(".*[^\\p{L}\\d\\s].*")) {
                throw new AppException(ErrorCode.INVALID_FULL_NAME);
            }
             */

            if (addressAccountRequest.getPhoneNumber().startsWith("-") || !addressAccountRequest.getPhoneNumber().matches("\\d+")) {
                throw new AppException(ErrorCode.INVALID_PHONE_NUMBER);
            }
            /*
            if (addressAccountRequest.getDetailedAddress().matches(".*[^\\p{L}\\d\\s].*")) {
                throw new AppException(ErrorCode.INVALID_DETAIL_ADDRESS);
            }
             */
            AddressAccount addressAccount = addressAccountMapper.toEntity(addressAccountRequest);
            addressAccount.setAccount(account);
            addressAccountRepository.save(addressAccount);
            return ApiResponse.<String>builder()
                    .message("Address created successfully")
                    .build();
        }catch (AppException e) {
            return ApiResponse.<String>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .message("Failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<String> updateAddress(Long id, AddressAccountRequest addressAccountRequest) {
        try {
            if (addressAccountRequest.getPhoneNumber().startsWith("-") || !addressAccountRequest.getPhoneNumber().matches("\\d+")) {
                throw new AppException(ErrorCode.INVALID_PHONE_NUMBER);
            }
            AddressAccount addressAccount = addressAccountRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
            addressAccount.setFullName(addressAccountRequest.getFullName());
            addressAccount.setPhoneNumber(addressAccountRequest.getPhoneNumber());
            addressAccount.setProvince(addressAccountRequest.getProvince());
            addressAccount.setDistrict(addressAccountRequest.getDistrict());
            addressAccount.setWard(addressAccountRequest.getWard());
            addressAccount.setDetailedAddress(addressAccountRequest.getDetailedAddress());
            addressAccountRepository.save(addressAccount);
            return ApiResponse.<String>builder()
                    .message("Address updated successfully")
                    .build();
        } catch (AppException e) {
            return ApiResponse.<String>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .code(101)
                    .message("Failed to update address: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<AddressAccountResponse>> getAllAddress(Pageable pageable) {
        try {
            String email = jwtProvider.getEmailContext();
            if (email == null || email.isEmpty()) {
                throw new AppException(ErrorCode.USER_NOT_FOUND);
            }
            Account account = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
            List<AddressAccount> addressList = addressAccountRepository.findByAccount(account, pageable);
            List<AddressAccountResponse> addressResponses = (addressList == null || addressList.isEmpty())
                    ? Collections.emptyList()
                    : addressList.stream().map(addressAccountMapper::toResponse).toList();

            return ApiResponse.<List<AddressAccountResponse>>builder()
                    .message("Fetched address list successfully")
                    .data(addressResponses)
                    .build();

        } catch (AppException e) {
            return ApiResponse.<List<AddressAccountResponse>>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .data(Collections.emptyList())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<AddressAccountResponse>>builder()
                    .message("Failed to fetch address list: " + e.getMessage())
                    .data(Collections.emptyList())
                    .build();
        }
    }



    @Override
    public ApiResponse<String> deleteAddress(Long id) {
        try {
            String email = jwtProvider.getEmailContext();
            Account account = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
            /*
            AddressAccount address = addressAccountRepository.findById(account.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
            AddressAccount addressAccount = addressAccountRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));

             */
            addressAccountRepository.deleteById(id);
            return ApiResponse.<String>builder()
                    .message("Address deleted successfully")
                    .build();
        } catch (AppException e) {
            return ApiResponse.<String>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .message("Failed to delete address: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<AddressAccountResponse> getAddressById(Long id) {
        try {
            Optional<AddressAccount> optionalAddressAccount = addressAccountRepository.findById(id);
            if (optionalAddressAccount.isEmpty()) {
                return ApiResponse.<AddressAccountResponse>builder()
                        .code(HttpStatus.NOT_FOUND.value())
                        .message("Address not found with id: " + id)
                        .build();
            }
            AddressAccount addressAccount = optionalAddressAccount.get();
            AddressAccountResponse addressResponses = addressAccountMapper.toResponse(addressAccount);
            return ApiResponse.<AddressAccountResponse>builder()
                    .message("Fetched address successfully")
                    .data(addressResponses)
                    .build();

        } catch (AppException e) {
            return ApiResponse.<AddressAccountResponse>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<AddressAccountResponse>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Failed to fetch address: " + e.getMessage())
                    .build();
        }
    }

}
