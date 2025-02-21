package org.example.cy_shop.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.cy_shop.configuration.jwtConfig.JwtProvider;
import org.example.cy_shop.dto.response.auth.IdentityVerificationResponse;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.IdentityVerification;
import org.example.cy_shop.entity.Shop;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.repository.IAccountRepository;
import org.example.cy_shop.repository.IIdentityVerificationRepository;
import org.example.cy_shop.repository.IShopRepository;
import org.example.cy_shop.service.IIdentityVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class IdentityVerificationServiceImpl implements IIdentityVerificationService {
    @Autowired
    private IIdentityVerificationRepository identityVerificationRepository;

    @Autowired
    private IAccountRepository accountRepository;

    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private IShopRepository shopRepository;

    @Autowired
    private ObjectMapper objectMapper;
    private static final String ID_CARD_IMAGE_DIR = "uploads/id-card-images/";


    @Override
    public IdentityVerificationResponse processIdentityData(String qrData) {
        IdentityVerificationResponse response = new IdentityVerificationResponse();
        try {
            String[] parts = qrData.split("\\|");
            if (parts.length < 5) {
                throw new RuntimeException("Invalid QR data format.");
            }
            String identityNumber = parts[0].trim();
            String fullName = parts[2].trim();
            String birthDateStr = parts[3].trim();
            String gender = parts[4].trim();
            String address = parts[5].trim();
            LocalDate birthDate = parseBirthDate(birthDateStr);
            if (identityVerificationRepository.existsByIdentityNumber(identityNumber)) {
                throw new RuntimeException("Identity number already exists: " + identityNumber);
            }
            response.setIdentityNumber(identityNumber);
            response.setFullName(fullName);
            response.setBirthDate(birthDate);
            response.setGender(gender);
            response.setAddress(address);
            response.setIsVerified(true);
        } catch (Exception e) {
            throw new RuntimeException("Error processing QR code data: " + e.getMessage());
        }
        return response;
    }


    private LocalDate parseBirthDate(String birthDateStr) {
        try {
            return LocalDate.parse(birthDateStr, DateTimeFormatter.ofPattern("ddMMyyyy"));
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Invalid birth date format: " + birthDateStr, e);
        }
    }

    @Override
    public void saveVerifiedIdentity(IdentityVerificationResponse response) {
        try {
            String email = jwtProvider.getEmailContext();
            if (email == null || email.isEmpty()) {
                throw new AppException(ErrorCode.USER_NOT_FOUND);
            }
            Account account = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
            if (identityVerificationRepository.existsByIdentityNumber(response.getIdentityNumber())) {
                throw new AppException(ErrorCode.IDENTITY_ALREADY_EXISTS);
            }
            IdentityVerification identityVerification = new IdentityVerification();
            identityVerification.setIdentityNumber(response.getIdentityNumber());
            identityVerification.setFullName(response.getFullName());
            identityVerification.setBirthDate(response.getBirthDate());
            identityVerification.setGender(response.getGender());
            identityVerification.setHomeTown(response.getAddress());
            identityVerification.setIsVerified(true);
            identityVerification.setCreatedAt(LocalDateTime.now());
            identityVerification.setUpdatedAt(LocalDateTime.now());
            identityVerification.setAccount(account);
            identityVerificationRepository.save(identityVerification);

            Shop shop = shopRepository.findByAccount(account)
                    .orElseThrow(() -> new AppException(ErrorCode.SHOP_NOT_FOUND));
            shop.setIsApproved(true);
            shop.setUpdatedAt(LocalDateTime.now());
            shopRepository.save(shop);
        } catch (Exception e) {
            throw new RuntimeException("Error saving verified identity: " + e.getMessage());
        }
    }

    private String saveIdCardImage(MultipartFile idCardImage) throws IOException {
        File dir = new File(ID_CARD_IMAGE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = System.currentTimeMillis() + "-" + idCardImage.getOriginalFilename();
        Path path = Paths.get(ID_CARD_IMAGE_DIR + fileName);
        Files.copy(idCardImage.getInputStream(), path);

        return path.toString();
    }

}
