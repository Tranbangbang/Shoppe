package org.example.cy_shop.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.auth.LogoutRequest;
import org.example.cy_shop.dto.request.auth.ResetPasswordRequest;
import org.example.cy_shop.dto.request.auth.SignInRequest;
import org.example.cy_shop.dto.request.auth.SignupRequest;
import org.example.cy_shop.dto.response.auth.AccountResponse;
import org.example.cy_shop.dto.response.auth.SignInResponse;
import org.example.cy_shop.dto.response.auth.UserResponse;
import org.example.cy_shop.entity.Account;
import org.springframework.http.ResponseEntity;

public interface IAccountService {
    Account getMyAccount();
    ApiResponse<AccountResponse> getMyAcc();
    AccountResponse findByEmail(String email);
    AccountResponse findByUserName(String userName);
    ResponseEntity<ApiResponse<AccountResponse>> registerAccount(SignupRequest signupRequest);
    ResponseEntity<ApiResponse<SignInResponse>> login(SignInRequest signinRequest, HttpServletRequest request);
    ResponseEntity<ApiResponse<SignInResponse>> loginForAdmin(SignInRequest signinRequest, HttpServletRequest request);
    ResponseEntity<ApiResponse<UserResponse>> getUserProfile();
    ResponseEntity<ApiResponse<?>> logout(LogoutRequest logoutRequest);
    ResponseEntity<ApiResponse<?>> logout();
    ResponseEntity<ApiResponse<?>> logout(String authorizationHeader);
    ResponseEntity<ApiResponse<AccountResponse>>  resetPassword(ResetPasswordRequest resetPasswordRequest);
}
