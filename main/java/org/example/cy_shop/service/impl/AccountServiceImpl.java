package org.example.cy_shop.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.example.cy_shop.configuration.AccountDetailsImpl;
import org.example.cy_shop.configuration.jwtConfig.JwtAuthenticationFilter;
import org.example.cy_shop.configuration.jwtConfig.JwtProvider;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.auth.LogoutRequest;
import org.example.cy_shop.dto.request.auth.ResetPasswordRequest;
import org.example.cy_shop.dto.request.auth.SignInRequest;
import org.example.cy_shop.dto.request.auth.SignupRequest;
import org.example.cy_shop.dto.response.auth.AccountResponse;
import org.example.cy_shop.dto.response.auth.SignInResponse;
import org.example.cy_shop.dto.response.auth.UserResponse;
import org.example.cy_shop.entity.*;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.AccountMapper;
import org.example.cy_shop.mapper.UserMapper;
import org.example.cy_shop.repository.*;
import org.example.cy_shop.service.IAccountService;
import org.example.cy_shop.service.IViaCodeService;
import org.example.cy_shop.utils.Const;
import org.example.cy_shop.utils.DeviceInfoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class AccountServiceImpl implements IAccountService, UserDetailsService {
    @Autowired
    private IAccountRepository accountRepository;
    @Autowired
    private IShopRepository shopRepository;
    @Autowired
    private IViaCodeService viaCodeService;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IRoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AccountMapper accountMapper;
    @Autowired
    private IRefreshTokenRepository refreshTokenRepository;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtProvider jwtUtils;
    @Autowired
    UserMapper userMapper;
    @Autowired
    IPasswordHistoryRepository passwordHistoryRepository;
    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private HttpServletRequest httpServletRequest;


    @Value("${jwt.JWT_EXPIRATION_REFRESH_TOKEN}")
    private int jwtExpirationRefreshToken;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return new AccountDetailsImpl(accountRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User not found with email: " + email)));
    }


    @Override
    public Account getMyAccount() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        var token = jwtAuthenticationFilter.getJwtFromRequest( request);
//        System.out.println("token = " + token);

        String email = jwtUtils.getEmailContext();
        if(email == null)
            throw new AppException(ErrorCode.USER_NOT_LOGIN);
        Account account = accountRepository.findByEmail(email).orElse(null);
        if(account == null)
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        return account;
    }

    @Override
    public ApiResponse<AccountResponse> getMyAcc() {
        try {
            Account account = getMyAccount();
            return new ApiResponse<>(200, "Thông tin acc", accountMapper.toResponse(account));
        }catch (Exception e){
            System.out.println("Loi khi convert entity sang response (acc service): " + e);
            return null;
        }
    }
    @Override
    public AccountResponse findByEmail(String email) {
        try {
            List<Account> accountList = accountRepository.findByEmailCustom(email);
            if (accountList.isEmpty()) {
                System.out.println("Không tìm thấy tài khoản với email: " + email);
                return null;
            }
            Account account = accountList.get(0);
            System.out.println("Tài khoản tìm được = " + account);
            return accountMapper.toResponse(account);
        } catch (Exception e) {
            System.out.println("Lỗi khi tìm kiếm theo email (AccountService): " + e.getMessage());
            return null;
        }
    }


    @Override
    public AccountResponse findByUserName(String userName) {
        try {
            System.out.println("Tìm theo user name: " + userName);
            return accountMapper.toResponse(accountRepository.findByUser_Username(userName).orElse(null));
        }catch (Exception e){
            System.out.println("Lỗi tìm account theo user name(account service): " + e);
            return null;
        }
    }

    @Override
    public ResponseEntity<ApiResponse<AccountResponse>> registerAccount(SignupRequest signupRequest) {
        try{
            if (!viaCodeService.validateCode(signupRequest.getCode(), signupRequest.getEmail())) {
                throw new AppException(ErrorCode.REGISTER_ACCOUNT_FAILED);
            }
            if (accountRepository.existsByEmail(signupRequest.getEmail())) {
                throw new AppException(ErrorCode.ACCOUNT_EMAIL_EXISTED);
            }
            /*
            if (!isValidPassword(signupRequest.getPassword())) {
                throw new AppException(ErrorCode.INVALID_PASSWORD);
            }

             */
            User user = User.builder()
                    .username(signupRequest.getUsername())
                    .dob(signupRequest.getDob())
                    .avatar("")
                    .build();
            User insertedUser = userRepository.save(user);
            if (insertedUser == null) {
                throw new AppException(ErrorCode.USER_CANT_CREATE_USER);
            }
            Role userRole = roleRepository.findByName(Const.ROLE_USER);
            if (userRole == null) {
                throw new AppException(ErrorCode.ROLE_NOT_FOUND);
            }

            Account account = Account.builder()
                    .user(insertedUser)
                    .username(signupRequest.getUsername())
                    .password(passwordEncoder.encode(signupRequest.getPassword()))
                    .email(signupRequest.getEmail())
                    .isActive(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            UserRole accountUserRole = new UserRole();
            accountUserRole.setAccount(account);
            accountUserRole.setRole(userRole);
            account.setUserRoles(Set.of(accountUserRole));
            Account savedAccount = accountRepository.save(account);
            return ResponseEntity.ok(
                    ApiResponse.<AccountResponse>builder()
                            .data(accountMapper.toResponse(savedAccount))
                            .message("Register successfully")
                            .build());
        }catch (AppException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<AccountResponse>builder()
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<AccountResponse>builder()
                            .message("An error.")
                            .build());
        }

    }

    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[!@#$%^&*(),.?\":{}|<>])(?=.{8,})";
        return password.matches(passwordRegex);
    }
    @Override
    public ResponseEntity<ApiResponse<SignInResponse>> login(SignInRequest signinRequest, HttpServletRequest request) {
        try {
            Authentication authentication = authenticateUser(signinRequest);
            AccountDetailsImpl accountDetails = (AccountDetailsImpl) authentication.getPrincipal();
            Account account = accountDetails.getAccount();

            if (!account.getIsActive()) {
                throw new AppException(ErrorCode.ACCOUNT_INACTIVE);
            }

            Long accountId = account.getId();
            LocalDateTime now = LocalDateTime.now();
            String deviceInfo = DeviceInfoUtil.getDeviceInfo(request);
            String accountToken = jwtUtils.generateTokenByUsername(accountDetails.getEmail());
            String refreshToken = jwtUtils.generateRefreshTokenByUsername(now, accountDetails.getEmail());

            saveOrUpdateRefreshToken(accountId, accountToken, now, deviceInfo);

            List<String> roleNames = Collections.singletonList(accountDetails.getRoleName());
            SignInResponse signinResponse = SignInResponse.builder()
                    .id(accountId)
                    .type("Bearer")
                    .accountToken(accountToken)
                    .refreshToken(refreshToken)
                    .username(accountDetails.getUsername())
                    .email(account.getEmail())
                    .isActive(account.getIsActive())
                    .avatar(account.getUser().getAvatar())
                    .roleName(roleNames)
                    .build();

            ApiResponse<SignInResponse> apiResponse = ApiResponse.<SignInResponse>builder()
                    .data(signinResponse)
                    .message("Login success")
                    .build();

            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } catch (UsernameNotFoundException e) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        } catch (Exception e) {
            throw new AppException(ErrorCode.LOGIN_FAILED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<SignInResponse>> loginForAdmin(SignInRequest signinRequest, HttpServletRequest request) {
        try {
            Authentication authentication = authenticateUser(signinRequest);
            AccountDetailsImpl accountDetails = (AccountDetailsImpl) authentication.getPrincipal();
            Account account = accountDetails.getAccount();
            if (!account.getIsActive()) {
                throw new AppException(ErrorCode.ACCOUNT_INACTIVE);
            }
            boolean isAdmin = accountDetails.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(Const.ROLE_ADMIN));
            if (!isAdmin) {
                throw new AppException(ErrorCode.ACCESS_DENIED);
            }
            Long accountId = account.getId();
            LocalDateTime now = LocalDateTime.now();
            String deviceInfo = DeviceInfoUtil.getDeviceInfo(request);
            String accountToken = jwtUtils.generateTokenByUsername(accountDetails.getEmail());
            String refreshToken = jwtUtils.generateRefreshTokenByUsername(now, accountDetails.getEmail());
            saveOrUpdateRefreshToken(accountId, accountToken, now, deviceInfo);
            List<String> roleNames = Collections.singletonList(accountDetails.getRoleName());
            SignInResponse signinResponse = SignInResponse.builder()
                    .id(accountId)
                    .type("Bearer")
                    .accountToken(accountToken)
                    .refreshToken(refreshToken)
                    .username(accountDetails.getUsername())
                    .email(account.getEmail())
                    .isActive(account.getIsActive())
                    .avatar(account.getUser().getAvatar())
                    .roleName(roleNames)
                    .build();
            ApiResponse<SignInResponse> apiResponse = ApiResponse.<SignInResponse>builder()
                    .data(signinResponse)
                    .message("Admin login success")
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } catch (UsernameNotFoundException e) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        } catch (AppException e) {
            throw e;
        } catch (Exception e) {
            throw new AppException(ErrorCode.LOGIN_FAILED);
        }
    }


    @Override
    public ResponseEntity<ApiResponse<UserResponse>> getUserProfile() {
        try {
            String email = jwtUtils.getEmailContext();
            if (email == null || email.isEmpty()) {
                throw new AppException(ErrorCode.INVALID_MISSING_TOKEN);
            }
            Account account = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            UserResponse userResponse = userMapper.toResponse(account);
            Shop shop = shopRepository.findByEmail(email);
            if (shop != null) {
                userResponse.setShopId(shop.getId());
                userResponse.setIsApprovedShop(shop.getIsApproved());
            } else {
                userResponse.setShopId(null);
            }

            ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                    .message("User details retrieved successfully.")
                    .data(userResponse)
                    .build();

            return ResponseEntity.ok(apiResponse);
        } catch (AppException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<UserResponse>builder()
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<UserResponse>builder()
                            .message("An error occurred while retrieving user details.")
                            .build());
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> logout(LogoutRequest logoutRequest) {
        try {
            String refreshToken = logoutRequest.getRequestToken();
            RefreshToken existingToken = refreshTokenRepository.getByToken(refreshToken);
            if (existingToken !=null) {
                refreshTokenRepository.delete(existingToken);
            } else {
                throw new AppException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
            }
            ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                    .message("Logout successful")
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);

        } catch (Exception e) {
            throw new AppException(ErrorCode.LOGOUT_FAILED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse<?>> logout() {
        try {
            String token = extractTokenFromRequest();
            if (token.isEmpty()) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }
            RefreshToken refreshToken = refreshTokenRepository.getByToken(token);
            if (refreshToken != null) {
                refreshTokenRepository.delete(refreshToken);
            } else {
                throw new AppException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
            }
            ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                    .message("Logout successful")
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);

        } catch (AppException e) {
            ApiResponse<Void> errorResponse = ApiResponse.<Void>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            throw new AppException(ErrorCode.LOGOUT_FAILED);
        }
    }

    private String extractTokenFromRequest() {
        String authorizationHeader = httpServletRequest.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        throw new AppException(ErrorCode.INVALID_TOKEN);
    }

    @Override
    public ResponseEntity<ApiResponse<?>> logout(String authorizationHeader) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new AppException(ErrorCode.INVALID_TOKEN);
            }
            String token = authorizationHeader.substring(7);
            RefreshToken refreshToken = refreshTokenRepository.getByToken(token);
            if (refreshToken != null) {
                refreshTokenRepository.delete(refreshToken);
            } else {
                throw new AppException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
            }
            ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                    .message("Logout successful")
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
        } catch (AppException e) {
            ApiResponse<Void> errorResponse = ApiResponse.<Void>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            throw new AppException(ErrorCode.LOGOUT_FAILED);
        }
    }


    @Override
    public ResponseEntity<ApiResponse<AccountResponse>> resetPassword(ResetPasswordRequest resetPasswordRequest) {
            String email = jwtUtils.getEmailContext();
            if (email == null || email.isEmpty()) {
                throw new AppException(ErrorCode.USER_NOT_FOUND);
            }
            Account account = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
            if (!passwordEncoder.matches(resetPasswordRequest.getCurrentPassword(), account.getPassword())) {
                throw new AppException(ErrorCode.CURRENT_PASSWORD_INCORRECT);
            }

            if (passwordEncoder.matches(resetPasswordRequest.getNewPassword(), account.getPassword())) {
                throw new AppException(ErrorCode.PASSWORD_MUST_BE_DIFFERENT);
            }
            PasswordHistory lastPasswordHistory = passwordHistoryRepository.findTop1ByAccountIdOrderByCreatedAtDesc(account.getId());
            if (lastPasswordHistory != null && passwordEncoder.matches(resetPasswordRequest.getNewPassword(), lastPasswordHistory.getPassword())) {
                throw new AppException(ErrorCode.PASSWORD_CANNOT_BE_OLD_PASSWORD);
            }
            PasswordHistory passwordHistory = new PasswordHistory();
            passwordHistory.setAccount(account);
            passwordHistory.setPassword(account.getPassword());
            passwordHistory.setCreatedAt(LocalDateTime.now());
            passwordHistoryRepository.save(passwordHistory);
            account.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
            accountRepository.save(account);

            ApiResponse<AccountResponse> apiResponse = ApiResponse.<AccountResponse>builder()
                    .code(200)
                    .message("Password changed successfully")
                    .build();
            return ResponseEntity.status(HttpStatus.OK).body(apiResponse);

    }


    private Authentication authenticateUser(SignInRequest signinRequest) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signinRequest.getEmail(), signinRequest.getPassword())
        );
    }

    public void saveOrUpdateRefreshToken(Long accountId, String refreshToken, LocalDateTime now, String deviceInfo) {
        RefreshToken existingToken = refreshTokenRepository.findByAccountIdAndDeviceInfo(accountId, deviceInfo);
        if (existingToken != null) {
            existingToken.setToken(refreshToken);
            existingToken.setCreatedAt(now);
            existingToken.setExp(now.plus(Duration.ofMillis(jwtExpirationRefreshToken)));
            existingToken.setIat(now);
            refreshTokenRepository.save(existingToken);
        } else {
            RefreshToken refreshTokenModel = RefreshToken.builder()
                    .accountId(accountId)
                    .token(refreshToken)
                    .createdAt(now)
                    .exp(now.plus(Duration.ofMillis(jwtExpirationRefreshToken)))
                    .iat(now)
                    .deviceInfo(deviceInfo)
                    .build();
            refreshTokenRepository.save(refreshTokenModel);
        }
    }
}
