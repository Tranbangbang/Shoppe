package org.example.cy_shop.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.auth.UserActiveRequest;
import org.example.cy_shop.dto.request.auth.UserRequest;
import org.example.cy_shop.dto.request.search.SearchAccount;
import org.example.cy_shop.dto.response.auth.AccountResponse;
import org.example.cy_shop.dto.response.product.ProductResponse;
import org.example.cy_shop.dto.response.statistical.StaticAccount;
import org.example.cy_shop.entity.User;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.service.IAccountService;
import org.example.cy_shop.service.IRoleService;
import org.example.cy_shop.service.IUserService;
import org.example.cy_shop.utils.Const;
import org.example.cy_shop.utils.UtilsFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "02. MANAGER_USER")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/manager_user")
public class ManagerUserController {
    @Autowired
    IAccountService accountService;
    @Autowired
    IUserService userService;
    @Autowired
    IRoleService roleService;
    @Autowired
    PasswordEncoder passwordEncoder;

    //----------------hiển thị và lọc user
    @Operation(
            summary = "Tìm theo name và email"
    )
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/list")
    public ApiResponse<Page<AccountResponse>> l(@RequestParam(value = "page", defaultValue = "1") int page,
                                                @RequestParam(value = "limit", required = false) Integer limit,
                                                @RequestParam(value = "username", required = false) String username,
                                                @RequestParam(value = "fullname", required = false) String fullName,
                                                @RequestParam(value = "role", required = false) String role,
                                                @RequestParam(value = "active", required = false) Boolean active){

        if(limit == null)
            limit = Const.RECORD_OF_TABLE_PAGE;
        Pageable pageable = PageRequest.of(page-1, limit, Sort.by(Sort.Order.desc("createdAt")));

        SearchAccount searchAccount = new SearchAccount();

        if(username != null)
            username = username.trim();
        if (username != null && username.length() != 0) {
            searchAccount.setUsername(username);
        }

        if(fullName != null)
            fullName = fullName.trim();
        if(fullName != null && fullName.length() != 0){
            searchAccount.setFullname(fullName);
        }

        if(role != null)
            role = role.trim();
        if(role != null && role.length()!=0)
            searchAccount.setRole(role);

        if(active != null )
            searchAccount.setIsActive(active);

        return userService.findCustome(searchAccount, pageable);
    }

    //-----------------thêm user
    @Operation(
            summary = "Thêm user"
    )
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/add")
    public ApiResponse<String> addAnUser(@RequestBody UserRequest userRequest){
        userRequest.simpleValidate();

        if(userRequest != null ){
            if(userRequest.getPassword() == null)
                throw new AppException(ErrorCode.PASS_WORD_NULL);

            //-------------check email--------------------
            if(userRequest.getEmail() == null) {
                throw new AppException(ErrorCode.EMAIL_NOT_VALID);
            }
            if(UtilsFunction.isEmail(userRequest.getEmail()) == false)
                throw new AppException(ErrorCode.EMAIL_NOT_VALID);
            AccountResponse account = accountService.findByEmail(userRequest.getEmail());
            if(account != null)
                throw new AppException(ErrorCode.EMAIL_WAS_REGISTER);


        }

        //-------------------check role
        if(userRequest != null) {
            if(userRequest.getRole() != null){
                for(var it: userRequest.getRole()){
                    if(roleService.findByName(it) == null)
                        throw new AppException(ErrorCode.ROLE_NOT_VALID);
                }
            }
        }


        if(userService.save(userRequest) == 1)
            return ApiResponse.<String>builder()
                .code(200)
                .message("Đăng ký tài khoản thành công")
                .build();

        return ApiResponse.<String>builder()
                .code(400)
                .message("Không thể thêm người dùng")
                .build();

    }

    @Operation(
            summary = "Update role (dùng id user)"
    )
    @PostMapping("/update_role")
    public ApiResponse<String> updateRoleUser(@RequestBody UserRequest userRequest){
        userRequest.simpleValidate();
        return userService.update(userRequest);
    }

    @PostMapping("/delete")
    public ApiResponse<String> deleteUser(@RequestBody UserRequest userRequest){
        return userService.delete(userRequest);
    }

    @PostMapping("/active_account")
    public ApiResponse<String> activeUser(@RequestBody UserActiveRequest userActiveRequest){
        return userService.active(userActiveRequest);
    }

    @GetMapping("/get_pass")
        public String getPass(){
        return passwordEncoder.encode("123");
    }

    @GetMapping("/count")
    public Long getCount(){
        return userService.count();
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SHOP')")
    @GetMapping("/my_account")
    public ApiResponse<User>  myAccount(){
        return  new ApiResponse<>(200, "Thông tin user", accountService.getMyAccount().getUser());
    }

    //---thông kê user
    @Operation(
            summary = "Thống kê user"
    )
    @GetMapping("/static_account")
    public ApiResponse<StaticAccount> staticAcc(){
        return userService.staticAccount();
    }

//    IProductRepository
}


