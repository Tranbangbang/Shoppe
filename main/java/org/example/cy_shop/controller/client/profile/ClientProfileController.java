package org.example.cy_shop.controller.client.profile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.response.auth.AccountResponse;
import org.example.cy_shop.service.IAccountService;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "USER_INFO")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/user_info")
public class ClientProfileController {
    @Autowired
    IAccountService accountService;

    //---lấy ra thông tin acc
    @Operation(
            summary = "Lấy thông tin acc đang đăng nhập"
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ApiResponse<AccountResponse> getMyInfo(){
        return accountService.getMyAcc();
    }
}
