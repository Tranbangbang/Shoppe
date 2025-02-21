package org.example.cy_shop.controller.user;


import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.auth.*;
import org.example.cy_shop.dto.response.auth.AccountResponse;
import org.example.cy_shop.dto.response.auth.SignInResponse;
import org.example.cy_shop.dto.response.auth.UserResponse;
import org.example.cy_shop.service.IAccountService;
import org.example.cy_shop.service.IAuthenticationService;
import org.example.cy_shop.service.IUserService;
import org.example.cy_shop.utils.Const;
import org.example.cy_shop.utils.LoginHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Tag(name = "01. AUTH")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/auth")
public class AuthController {
    @Autowired
    IAuthenticationService authenticationService;
    @Autowired
    IUserService userService;
    @Autowired
    IAccountService accountService;
    @Autowired
    LoginHelper loginHelper;

    @PostMapping("/via-email")
    public ApiResponse<String> viaEmailToResgister(@RequestBody String email) {
        return authenticationService.handleSendCodeToMail(email);
    }
    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<AccountResponse>> signUp(@RequestBody SignupRequest signupRequest) {
        return accountService.registerAccount(signupRequest);
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<SignInResponse>> login(@RequestBody SignInRequest signInRequest, HttpServletRequest request) {
        return accountService.login(signInRequest, request);
    }
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getUserProfile() {
        return accountService.getUserProfile();
    }

    @GetMapping("/forgot-password")
    public ApiResponse<?> forgotPassword(@RequestParam String email) throws MessagingException {
        return authenticationService.sendTokenForgotPassword(email);
    }

    @PostMapping("/forgot-password")
    public ApiResponse<AccountResponse> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        return authenticationService.resetPassword(changePasswordRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest signInRequest) {
        return accountService.logout(signInRequest);
    }



    /*
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout() {
        return accountService.logout();
    }

     */



    @PostMapping("/update-profile")
    public ApiResponse<UserResponse> updateProfile(@ModelAttribute UpdateProfileRequest profileRequest) {
            return userService.updateProfile(profileRequest);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<AccountResponse>> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        return accountService.resetPassword(resetPasswordRequest);
    }
    @GetMapping("/oauth/google")
    public void loginWithGoogle(@RequestParam("code") String code,
                                      @RequestParam("scope") String scope,
                                      @RequestParam("authuser") String authUser,
                                      @RequestParam("prompt") String prompt,
                                      HttpServletResponse response) throws IOException {

        SignInResponse signInResponse = loginHelper.processGrantCode(code);
        if (signInResponse != null) {
            String redirectUrl = "https://team02.cyvietnam.id.vn/vi/login-success?token=" + signInResponse.getAccountToken()
                    + "&refreshToken=" + signInResponse.getRefreshToken();
            response.sendRedirect(redirectUrl);
        } else {
            String redirectUrl = "https://team02.cyvietnam.id.vn/vi/login-success";
            response.sendRedirect(redirectUrl);
        }
    }
}
