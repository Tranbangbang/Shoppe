package org.example.cy_shop.service;

import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.auth.UpdateProfileRequest;
import org.example.cy_shop.dto.request.auth.UserActiveRequest;
import org.example.cy_shop.dto.request.auth.UserRequest;
import org.example.cy_shop.dto.request.search.SearchAccount;
import org.example.cy_shop.dto.response.auth.AccountResponse;
import org.example.cy_shop.dto.response.auth.UserResponse;
import org.example.cy_shop.dto.response.statistical.StaticAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserService {
    ApiResponse<Page<AccountResponse>> findCustome(SearchAccount searchAccount, Pageable pageable);
    ApiResponse<StaticAccount> staticAccount();
    int save(UserRequest userRequest);
    Long count();
    ApiResponse<String> update(UserRequest userRequest);
    ApiResponse<String> delete(UserRequest userRequest);
    ApiResponse<String> active(UserActiveRequest userActiveRequest);
    ApiResponse<UserResponse> updateProfile(UpdateProfileRequest profileRequest);
}
