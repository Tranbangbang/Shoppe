package org.example.cy_shop.controller.Shop;

import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.response.auth.IdentityVerificationResponse;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.service.impl.IdentityVerificationServiceImpl;
import org.example.cy_shop.utils.QRCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/identity-verification")
public class IdentityVerificationController {
    @Autowired
    private IdentityVerificationServiceImpl identityVerificationService;
    @Autowired
    private QRCodeUtil qrCodeUtil;


    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<IdentityVerificationResponse>> submitIdentityVerification(
            @RequestParam("idCardImage") MultipartFile idCardImage) {
        try {
            String decodedData = qrCodeUtil.decodeQRCode(idCardImage);
            if (decodedData == null) {
                throw new AppException(ErrorCode.decodedDataNull);
            }
            IdentityVerificationResponse response = identityVerificationService.processIdentityData(decodedData);
            return ResponseEntity.ok(
                    ApiResponse.<IdentityVerificationResponse>builder()
                            .data(response)
                            .message("Success")
                            .build()
            );
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<IdentityVerificationResponse>builder()
                            .code(400)
                            .message("Error processing identity verification: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping("/confirm")
    public ApiResponse<String> confirmIdentityVerification(@RequestBody IdentityVerificationResponse response) {
        try {
            identityVerificationService.saveVerifiedIdentity(response);
            return ApiResponse.<String>builder()
                    .data("Identity verification successful")
                    .message("Your identity has been successfully verified.")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .code(500)
                    .message("Error confirming identity verification: " + e.getMessage())
                    .build();
        }
    }

}
