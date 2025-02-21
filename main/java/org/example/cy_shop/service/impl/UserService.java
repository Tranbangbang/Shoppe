package org.example.cy_shop.service.impl;

import jakarta.transaction.Transactional;
import org.example.cy_shop.configuration.jwtConfig.JwtProvider;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.auth.UpdateProfileRequest;
import org.example.cy_shop.dto.request.auth.UserActiveRequest;
import org.example.cy_shop.dto.request.auth.UserRequest;
import org.example.cy_shop.dto.request.search.SearchAccount;
import org.example.cy_shop.dto.response.auth.AccountResponse;
import org.example.cy_shop.dto.response.auth.UserResponse;
import org.example.cy_shop.dto.response.statistical.StaticAccount;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.Role;
import org.example.cy_shop.entity.User;
import org.example.cy_shop.entity.UserRole;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.AccountMapper;
import org.example.cy_shop.mapper.UserMapper;
import org.example.cy_shop.repository.IAccountRepository;
import org.example.cy_shop.repository.IRoleRepository;
import org.example.cy_shop.repository.IUserRepository;
import org.example.cy_shop.repository.IUserRoleRepository;
import org.example.cy_shop.service.IAuthenticationService;
import org.example.cy_shop.service.IRoleService;
import org.example.cy_shop.service.IUserService;
import org.example.cy_shop.utils.Const;
import org.example.cy_shop.utils.UtilsFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserService implements IUserService {
    @Autowired
    IUserRepository userRepository;
    @Autowired
    IAccountRepository accountRepository;
    @Autowired
    IRoleRepository roleRepository;
    @Autowired
    IRoleService roleService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    IUserRoleRepository userRoleRepository;
    @Autowired
    AccountMapper accountMapper;
    @Autowired
    IAuthenticationService authenticationService;

    @Autowired
    JwtProvider jwtProvider;
    @Override
    public ApiResponse<UserResponse> updateProfile(UpdateProfileRequest profileRequest) {
        try {
            String email = jwtProvider.getEmailContext();
            if (email == null || email.isEmpty()) {
                throw new AppException(ErrorCode.INVALID_MISSING_TOKEN);
            }
            Account account = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            account.getUser().setUsername(profileRequest.getUsername());
            account.setUsername(profileRequest.getUsername());
            account.setEmail(profileRequest.getEmail());
            account.getUser().setDob(profileRequest.getDob());
            account.getUser().setGender(profileRequest.getGender());
            account.getUser().setLocation(profileRequest.getLocation());
            if (profileRequest.getFile() != null) {
                String avatarUrl = saveAvatarFile(profileRequest.getFile());
                account.getUser().setAvatar(avatarUrl);
            }
            userRepository.save(account.getUser());
            accountRepository.save(account);
            UserResponse userResponse = userMapper.toResponse(account);
            return ApiResponse.<UserResponse>builder()
                    .code(200)
                    .message("Cập nhật thông tin người dùng thành công")
                    .data(userResponse)
                    .build();

        } catch (Exception e) {
            System.out.println("Lỗi khi cập nhật profile: " + e);
            return ApiResponse.<UserResponse>builder()
                    .code(400)
                    .message("Lỗi khi cập nhật thông tin người dùng")
                    .build();
        }
    }



    private String saveAvatarFile(MultipartFile file) {
        String avatarDirectory = Const.FOLDER_MEDIA_AVATAR;
        String avatarFileName = UtilsFunction.saveMediaFile(file, avatarDirectory);
        if (avatarFileName != null) {
            return Const.PREFIX_URL_AVATAR + avatarFileName;
        } else {
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    @Override
    public ApiResponse<Page<AccountResponse>> findCustome(SearchAccount searchAccount, Pageable pageable) {
        try {
            Page<AccountResponse> accountResponsePage =
                    accountRepository.findAccountCustom(searchAccount.getUsername(), searchAccount.getFullname(), searchAccount.getRole(),searchAccount.getIsActive(),  pageable)
                    .map(it -> accountMapper.toResponse(it));

            return ApiResponse.<Page<AccountResponse>>builder()
                    .code(200)
                    .message("Danh sách user")
                    .data(accountResponsePage)
                    .build();
        }catch (Exception e){
            System.out.println("Lỗi tìm kiếm user (user service): " + e);
            return ApiResponse.<Page<AccountResponse>>builder()
                    .code(400)
                    .message("Không thể hiển thị user")
                    .build();
        }
    }

    @Override
    public ApiResponse<StaticAccount> staticAccount() {
        Long accountActive = accountRepository.findStaticAllActive();
        Long accountInactive = accountRepository.findStaticAllInActive();
        Long allAccount = accountRepository.findStaticAllAccount();

        LocalDate today = UtilsFunction.getVietNameTimeNow().toLocalDate();
        LocalDate preDay = today.minusDays(1);
        Long allAccountToday = accountRepository.findStaticAllNewAccount(today);
//        Long allAccoutPreday = accountRepository.findStaticAllNewAccount(preDay);

        Double percent = null;
        if(allAccount != 0){
            percent = (allAccountToday/allAccount)*100.0;
        }

        StaticAccount result  = StaticAccount.builder()
                .totalAcc(allAccount)
                .activeAcc(accountActive)
                .blockAcc(accountInactive)
                .todayNewAcc(allAccountToday)
//                .previousDateAcc(allAccoutPreday)
                .percentNewAcc( percent )
                .build();

        return new ApiResponse<>(200, "Thống kê account", result);
    }

    @Override
    public int save(UserRequest userRequest) {
        try {
            userRequest.setCreatedAt(UtilsFunction.getVietNameTimeNow());
            User user =  userRepository.save(userMapper.convertToUserEntity(userRequest));
            userRequest.setId(user.getId());
            Account account = accountRepository.save(userMapper.convertToAcountEntity(userRequest));

            User userSave= userMapper.convertToUserEntity(userRequest);
//            System.out.println("user name: " + userSave.getAvatar() + ", " + userSave.getName());

            String htmlContent = "<html>" +
                    "<head><style>" +
                    "body { font-family: Arial, sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }" +
                    "h1 { color: #4CAF50; }" +
                    "p { font-size: 16px; color: #333; }" +
                    ".container { background-color: #fff; padding: 20px; margin: 30px auto; width: 80%; max-width: 600px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); }" +
                    ".header { text-align: center; padding-bottom: 20px; }" +
                    ".footer { text-align: center; font-size: 14px; color: #888; margin-top: 20px; }" +
                    ".code-box { background-color: #f9f9f9; padding: 10px; border: 1px solid #ddd; border-radius: 5px; font-family: 'Courier New', monospace; margin-top: 10px; }" +
                    "a { color: #4CAF50; text-decoration: none; font-weight: bold; }" +
                    "</style></head>" +
                    "<body>" +
                    "<div class='container'>" +
                    "<div class='header'>" +
                    "<h1>Chúc mừng bạn đã tạo tài khoản thành công!</h1>" +
                    "</div>" +
                    "<p>Xin chào <strong>" + userRequest.getUsername() + "</strong>,</p>" +
                    "<p>Chúng tôi rất vui khi thông báo rằng tài khoản của bạn đã được tạo thành công trên hệ thống của chúng tôi.</p>" +
                    "<p><strong>Thông tin tài khoản:</strong></p>" +
                    "<div class='code-box'>" +
                    "<p><strong>Username:</strong> " + userRequest.getEmail() + "</p>" +
                    "<p><strong>Password:</strong> " + userRequest.getPassword() + "</p>" +
                    "</div>" +
                    "<p>Để bảo mật tài khoản của bạn, chúng tôi khuyến khích bạn thay đổi mật khẩu ngay khi có thể.</p>" +
                    "<div class='footer'>" +
                    "<p>Đội ngũ hỗ trợ khách hàng - Cảm ơn bạn đã chọn dịch vụ của chúng tôi.</p>" +
                    "</div>" +
                    "</div>" +
                    "</body>" +
                    "</html>";

            try {
                authenticationService.sendCodeToEmail(userRequest.getEmail(), "Đăng ký tài khoản thành công", htmlContent);
            }catch (Exception e){
                System.out.println("Loi gui email: " + e);
            }

            return 1;
        }catch (Exception e){
            System.out.println("Lỗi không thể thêm user(user service): " + e);
            return 0;
        }
    }

    @Override
    public Long count() {
        try {
            return userRepository.count();
        }catch (Exception e){
            System.out.println("Loi khong the dem user (user service): " + e);
            return 0L;

        }
    }

    @Override
    public ApiResponse<String> update(UserRequest userRequest) {
        Account account = accountRepository.findByUserIdCustom(userRequest.getId());

        Set<Role> role = new HashSet<>();
        if(account == null){
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        Long acountId = account.getId();
        List<UserRole> userRolesToDelete = userRoleRepository.findByAccountId(acountId);

        if(userRequest != null && userRequest.getRole() != null){
            for(var it: userRequest.getRole()){
                if(roleService.findByName(it) == null){
                    throw new AppException(ErrorCode.ROLE_NOT_FOUND);
                }
                Role roleTmp = roleRepository.findByName(it);
                role.add(roleTmp);
            }
        }


        Set<UserRole> userRoles = new HashSet<>();
        for(var it: role) {
            UserRole userRoleTmp = new UserRole();
            userRoleTmp.setAccount(account);
            userRoleTmp.setRole(it);
            userRoles.add(userRoleTmp);
        }
        account.setUserRoles(userRoles);
        account.setUpdatedAt(UtilsFunction.getVietNameTimeNow());

        accountRepository.save(account);

        //-----------------------xóa user_role đã tồn tại
        for(var it: userRolesToDelete){
            userRoleRepository.delete(it);
        }

        return ApiResponse.<String>builder()
                .code(200)
                .message("Cập nhật người dùng thành công")
                .build();

    }

    @Override
    public ApiResponse<String> delete(UserRequest userRequest) {
        Account account = accountRepository.findByUserIdCustom(userRequest.getId());
        if(account == null)
            throw new AppException(ErrorCode.ACCOUNT_OF_USER_NOT_FOUND);

        account.setIsActive(false);
        account.setUpdatedAt(UtilsFunction.getVietNameTimeNow());
        accountRepository.save(account);

        Account accSave = accountRepository.save(account);
        if(accSave != null)
            return new ApiResponse<>(200, "Khóa tài khoản thành công", null);
        else
            return new ApiResponse<>(400, "Lỗi không xác định", null);

    }

    @Override
    public ApiResponse<String> active(UserActiveRequest userActiveRequest) {
        Account account = accountRepository.findByUserIdCustom(userActiveRequest.getIdUser());
        if(account == null)
            throw new AppException(ErrorCode.ACCOUNT_OF_USER_NOT_FOUND);
        account.setIsActive(true);
        account.setUpdatedAt(UtilsFunction.getVietNameTimeNow());

        Account accSave = accountRepository.save(account);
        if(accSave != null)
            return new ApiResponse<>(200, "Mở khóa tài khoản thành công", null);
        else
            return new ApiResponse<>(400, "Lỗi không xác định", null);
    }

}
