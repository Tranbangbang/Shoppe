package org.example.cy_shop.service;

import jakarta.mail.MessagingException;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.auth.ChangePasswordRequest;
import org.example.cy_shop.dto.response.auth.AccountResponse;
import org.example.cy_shop.entity.ViaCode;

public interface IAuthenticationService {
    int generateCode();

    int sendCodeToEmail(String to, String subject, String content);

    ViaCode insertCode(int code, String email);

    ApiResponse<String> handleSendCodeToMail(String email);

    ApiResponse<String> sendTokenForgotPassword(String email) throws MessagingException;
    ApiResponse<AccountResponse> resetPassword(ChangePasswordRequest changePasswordRequest);
}
