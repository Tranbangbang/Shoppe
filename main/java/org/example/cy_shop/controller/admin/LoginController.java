package org.example.cy_shop.controller.admin;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.auth.SignInRequest;
import org.example.cy_shop.dto.response.auth.SignInResponse;
import org.example.cy_shop.service.IAccountService;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "ADMIN_04. Login")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/auth")
public class LoginController {
    @Autowired
    IAccountService accountService;
    @PostMapping("/admin/login")
    public ResponseEntity<ApiResponse<SignInResponse>> loginForAdmin(
            @RequestBody SignInRequest signinRequest,
            HttpServletRequest request) {
        return accountService.loginForAdmin(signinRequest, request);
    }
}
