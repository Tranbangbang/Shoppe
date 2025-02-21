package org.example.cy_shop.service;

import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.address.AddressAccountRequest;
import org.example.cy_shop.dto.response.address.AddressAccountResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IAddressAccountService {
    ApiResponse<String> createAddress(AddressAccountRequest addressAccountRequest);
    ApiResponse<String> updateAddress(Long id, AddressAccountRequest addressAccountRequest);
    ApiResponse<List<AddressAccountResponse>> getAllAddress(Pageable pageable);
    ApiResponse<String> deleteAddress(Long id);
    ApiResponse<AddressAccountResponse> getAddressById(Long id);
}
