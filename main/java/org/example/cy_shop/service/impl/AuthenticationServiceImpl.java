package org.example.cy_shop.service.impl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.example.cy_shop.configuration.jwtConfig.JwtProvider;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.auth.ChangePasswordRequest;
import org.example.cy_shop.dto.response.auth.AccountResponse;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.ViaCode;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.AccountMapper;
import org.example.cy_shop.repository.IAccountRepository;
import org.example.cy_shop.repository.IViaCodeRepository;
import org.example.cy_shop.service.IAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthenticationServiceImpl implements IAuthenticationService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private IViaCodeRepository viaCodeRepository;
    @Autowired
    private IAccountRepository accountRepository;
    @Autowired
    private JwtProvider jwtProvider;

    @Override
    public int generateCode() {
        return ThreadLocalRandom.current().nextInt(100000, 1000000);
    }

    @Override
    public int sendCodeToEmail(String to, String subject, String content) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            javaMailSender.send(message);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public ViaCode insertCode(int code, String email) {
        ViaCode viaCode = ViaCode.builder()
                .viaCode(code)
                .email(email.trim())
                .createdAt(LocalDateTime.now())
                .build();
        return viaCodeRepository.save(viaCode);
    }

    @Override
    public ApiResponse<String> handleSendCodeToMail(String email) {
        email = email.replaceAll("^\"|\"$", "");
        Optional<Account> account = accountRepository.findByEmail(email);
        if(account.isPresent()){
            throw new AppException(ErrorCode.email_already_exists);
        }
        try {
            int code = generateCode();
            int result = sendCodeToEmail(email, "Code to verify account", "Your sign-up code is: " + code);
            if (result == 1) {
                insertCode(code, email);
                return ApiResponse.<String>builder()
                        .code(200)
                        .message("Verification code sent successfully.")
                        .data("send code to email successfully")
                        .build();
            } else {
                return ApiResponse.<String>builder()
                        .code(500)
                        .message("Failed to send email.")
                        .data("Failed to send email. Please try again later.")
                        .build();
            }
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .code(500)
                    .message("send code to email failed")
                    .data(e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<String> sendTokenForgotPassword(String email) throws MessagingException {
        Optional<Account> optional = accountRepository.findByEmail(email);
        if (optional.isEmpty()) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        Account account = optional.get();
        String token = jwtProvider.generateForgotPasswordTokenByUsername(account.getUser().getUsername());

        account.setForgotPasswordToken(token);
        accountRepository.saveAndFlush(account);

        String subtitle = "Forgot Password";
        String passwordResetLink = "https://team02.cyvietnam.id.vn/vi/setNewPassword?token=" + token;
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <title>Password Reset</title>\n" +
                "  <style>\n" +
                "    body {\n" +
                "      font-family: 'Helvetica Neue', Arial, sans-serif;\n" +
                "      background-color: #eef2f7;\n" +
                "      color: #2c3e50;\n" +
                "      margin: 0;\n" +
                "      padding: 0;\n" +
                "      line-height: 1.6;\n" +
                "    }\n" +
                "    .container {\n" +
                "      max-width: 600px;\n" +
                "      margin: 50px auto;\n" +
                "      background-color: #ffffff;\n" +
                "      padding: 30px;\n" +
                "      border-radius: 10px;\n" +
                "      box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);\n" +
                "    }\n" +
                "    .header {\n" +
                "      text-align: center;\n" +
                "      margin-bottom: 20px;\n" +
                "    }\n" +
                "    .header h2 {\n" +
                "      color: #007bff;\n" +
                "      margin: 0;\n" +
                "    }\n" +
                "    .content {\n" +
                "      margin-bottom: 20px;\n" +
                "    }\n" +
                "    .button {\n" +
                "      display: block;\n" +
                "      text-align: center;\n" +
                "      margin: 20px 0;\n" +
                "    }\n" +
                "    .button a {\n" +
                "      display: inline-block;\n" +
                "      padding: 12px 25px;\n" +
                "      font-size: 16px;\n" +
                "      color: #ffffff;\n" +
                "      background-color: #007bff;\n" +
                "      border-radius: 5px;\n" +
                "      text-decoration: none;\n" +
                "      transition: background-color 0.3s;\n" +
                "    }\n" +
                "    .button a:hover {\n" +
                "      background-color: #0056b3;\n" +
                "    }\n" +
                "    .footer {\n" +
                "      text-align: center;\n" +
                "      font-size: 12px;\n" +
                "      color: #7f8c8d;\n" +
                "      margin-top: 20px;\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"container\">\n" +
                "    <div class=\"header\">\n" +
                "      <h2>Password Reset Request</h2>\n" +
                "    </div>\n" +
                "    <div class=\"content\">\n" +
                "      <p>Hello,</p>\n" +
                "      <p>We received a request to reset your password. Click the button below to set a new password:</p>\n" +
                "    </div>\n" +
                "    <div class=\"button\">\n" +
                "      <a href=\"" + passwordResetLink + "\">Reset Password</a>\n" +
                "    </div>\n" +
                "    <div class=\"content\">\n" +
                "      <p>If you didn’t request a password reset, please ignore this email. This link will expire in 24 hours.</p>\n" +
                "      <p>Thank you, <br>Your Website Team</p>\n" +
                "    </div>\n" +
                "    <div class=\"footer\">\n" +
                "      <p>&copy; 2024 CY VIETNAM. All rights reserved.</p>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>\n";


        helper.setTo(email);
        helper.setSubject(subtitle);
        helper.setText(html, true);

        javaMailSender.send(message);
        return ApiResponse.<String>builder()
                .code(200)
                .message("Password reset token sent successfully.")
                .data("Password reset token sent to " + email)
                .build();
    }

    @Override
    public ApiResponse<AccountResponse> resetPassword(ChangePasswordRequest changePasswordRequest) {
        String username = jwtProvider.getKeyByValueFromJWT(
                jwtProvider.getJWT_SECRET_FORGOT_PASSWORD_TOKEN(),
                "username",
                changePasswordRequest.getToken(),
                String.class
        );
        Optional<Account> optional = accountRepository.findByUser_Username(username);
        if (optional.isEmpty()) {
            throw new AppException(ErrorCode.TOKEN_RESET_PASSWORD_INVALID);
        }
        Account account = optional.get();

        if (account.getForgotPasswordToken() == null) {
            throw new AppException(ErrorCode.TOKEN_RESET_PASSWORD_INVALID);
        }

        if (!validateToken(jwtProvider.getJWT_SECRET_FORGOT_PASSWORD_TOKEN(), changePasswordRequest.getToken())) {
            throw new AppException(ErrorCode.TOKEN_RESET_PASSWORD_INVALID);
        }

        Date expDate = jwtProvider.getKeyByValueFromJWT(
                jwtProvider.getJWT_SECRET_FORGOT_PASSWORD_TOKEN(),
                "exp",
                changePasswordRequest.getToken(),
                Date.class
        );

        if (expDate == null) {
            throw new AppException(ErrorCode.TOKEN_RESET_PASSWORD_INVALID);
        }

        LocalDateTime exp = expDate.toInstant()
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime();
        if (exp.isBefore(LocalDateTime.now())) {
            throw new AppException(ErrorCode.TOKEN_RESET_PASSWORD_EXPIRED);
        }

        account.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        account.setForgotPasswordToken(null);
        account = accountRepository.save(account);

        return ApiResponse.<AccountResponse>builder()
                .code(200)
                .message("Password reset successfully.")
                .data(accountMapper.toResponse(account))
                .build();
    }

    private boolean validateToken(String jwtTokenSecret, String token) {
        try {
            Jwts.parser().setSigningKey(jwtTokenSecret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            return false;
        }
    }
}
