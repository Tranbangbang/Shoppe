package org.example.cy_shop.service;

import org.example.cy_shop.dto.response.auth.IdentityVerificationResponse;

public interface IIdentityVerificationService {

    IdentityVerificationResponse processIdentityData(String qrData);
    void saveVerifiedIdentity(IdentityVerificationResponse response);
}
