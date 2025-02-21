package org.example.cy_shop.utils;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import lombok.experimental.NonFinal;
import org.example.cy_shop.configuration.jwtConfig.JwtProvider;
import org.example.cy_shop.dto.request.auth.ModelAccountGoogle;
import org.example.cy_shop.dto.response.auth.SignInResponse;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.Role;
import org.example.cy_shop.entity.User;
import org.example.cy_shop.entity.UserRole;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.AccountMapper;
import org.example.cy_shop.repository.IAccountRepository;
import org.example.cy_shop.repository.IRoleRepository;
import org.example.cy_shop.repository.IUserRepository;
import org.example.cy_shop.service.impl.AccountServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@Transactional

public class LoginHelper {
    @Autowired
    IUserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    IAccountRepository accountRepository;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    IRoleRepository roleRepository;
    @Autowired
    AccountMapper accountMapper;
    @Autowired
    AccountServiceImpl accountService;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    String clientId;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    String clientSecret;

    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    String redirectUrl;

    private String getOauthAccessTokenGoogle(String code) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("redirect_uri", redirectUrl);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("scope", "https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile");
        params.add("scope", "https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email");
        params.add("scope", "openid");
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, httpHeaders);

        String url = "https://oauth2.googleapis.com/token";
        String response = restTemplate.postForObject(url, requestEntity, String.class);
        JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);

        return jsonObject.get("access_token").toString().replace("\"", "");
    }

    public SignInResponse processGrantCode(String code) {
        String accessToken = getOauthAccessTokenGoogle(code);

        ModelAccountGoogle modelAccountGoogle = getProfileDetailsGoogle(accessToken);
        Optional<Account> account = accountRepository.findByEmail(modelAccountGoogle.getEmail());

        if (account.isPresent()) {
            Account acc = account.get();

            String roleName = acc.getUserRoles().stream()
                    .map(userRole -> userRole.getRole().getName())
                    .findFirst()
                    .orElse("DEFAULT_ROLE");

            //String accountToken = jwtProvider.generateTokenByUsername(acc.getEmail());
           // String refreshToken = jwtProvider.generateRefreshTokenByUsername(LocalDateTime.now(), acc.getEmail());

            Long accountId = acc.getId();
            LocalDateTime now = LocalDateTime.now();
            String deviceInfo = "null";
            String accountToken = jwtProvider.generateTokenByUsername(acc.getEmail());
            String refreshToken = jwtProvider.generateRefreshTokenByUsername(now, acc.getEmail());

            accountService.saveOrUpdateRefreshToken(accountId, accountToken, now, deviceInfo);
            return SignInResponse.builder()
                    .id(acc.getId())
                    .type("Bearer")
                    .accountToken(accountToken)
                    .refreshToken(refreshToken)
                    .username(acc.getUser().getUsername())
                    .email(acc.getEmail())
                    .isActive(acc.getIsActive())
                    .avatar(acc.getUser().getAvatar())
                    .roleName(Collections.singletonList(roleName))
                    .build();
        }

        Account accountRegister = registerAccount(modelAccountGoogle);
        String accountToken = jwtProvider.generateTokenByUsername(accountRegister.getEmail());
        String refreshToken = jwtProvider.generateRefreshTokenByUsername(LocalDateTime.now(), accountRegister.getEmail());

        return SignInResponse.builder()
                .id(accountRegister.getId())
                .type("Bearer")
                .accountToken(accountToken)
                .refreshToken(refreshToken)
                .username(accountRegister.getUser().getUsername())
                .email(accountRegister.getEmail())
                .isActive(accountRegister.getIsActive())
                .avatar(accountRegister.getUser().getAvatar())
                .roleName(Collections.singletonList(accountRegister.getUserRoles().stream()
                        .map(userRole -> userRole.getRole().getName())
                        .findFirst()
                        .orElse("DEFAULT_ROLE")))
                .build();
    }


    private ModelAccountGoogle getProfileDetailsGoogle(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

        String url = "https://www.googleapis.com/oauth2/v2/userinfo";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        JsonObject jsonObject = new Gson().fromJson(response.getBody(), JsonObject.class);

        return ModelAccountGoogle.builder()
                .name(jsonObject.get("email").toString().replace("\"", ""))
                .username(jsonObject.get("name").toString().replace("\"", ""))
                .email(jsonObject.get("email").toString().replace("\"", ""))
                .password(UUID.randomUUID().toString())
                .avatar(jsonObject.get("picture").toString().replace("\"", ""))
                .dob(LocalDate.now())
                .gender(1)
                .build();

    }

    public Account registerAccount(ModelAccountGoogle modelAccountGoogle) {
        if (userRepository.existsByUsername(modelAccountGoogle.getUsername())) {
            throw new AppException(ErrorCode.USER_USERNAME_EXISTED);
        }
        if (accountRepository.existsByEmail(modelAccountGoogle.getEmail())) {
            throw new AppException(ErrorCode.ACCOUNT_EMAIL_EXISTED);
        }

        User user = User.builder()
                .username(modelAccountGoogle.getUsername())
                .name(modelAccountGoogle.getName())
                .gender(modelAccountGoogle.getGender())
                .dob(modelAccountGoogle.getDob())
                .avatar(modelAccountGoogle.getAvatar())
                .avatar("")
                .build();

        User insertUser = userRepository.saveAndFlush(user);

        if (insertUser == null) {
            throw new AppException(ErrorCode.USER_CANT_CREATE_USER);
        }

        Role userRole = roleRepository.findByName(Const.ROLE_USER);
        if (userRole == null) {
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }

        Account account = Account.builder()
                .user(insertUser)
                .password(passwordEncoder.encode(modelAccountGoogle.getPassword()))
                .email(modelAccountGoogle.getEmail())
                .isActive(true)
                .build();
        UserRole accountUserRole = new UserRole();
        accountUserRole.setAccount(account);
        accountUserRole.setRole(userRole);
        account.setUserRoles(Set.of(accountUserRole));
        return accountRepository.saveAndFlush(account);
    }
}
