package org.example.cy_shop.service.impl;

import org.example.cy_shop.configuration.jwtConfig.JwtProvider;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.response.notification.NotificationResponse;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.Notification;
import org.example.cy_shop.entity.Shop;
import org.example.cy_shop.enums.EnumNotificationStatus;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.NotificationMapper;
import org.example.cy_shop.repository.IAccountRepository;
import org.example.cy_shop.repository.INotificationRepository;
import org.example.cy_shop.repository.IShopRepository;
import org.example.cy_shop.service.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements INotificationService {
    @Autowired
    private INotificationRepository notificationRepository;
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private IAccountRepository accountRepository;
    @Autowired
    private IShopRepository shopRepository;
    @Override
    public ApiResponse<List<NotificationResponse>> getNotification() {
        try {
            List<Notification> notificationList = notificationRepository.findAll();
            if (notificationList.isEmpty()) {
                return ApiResponse.<List<NotificationResponse>>builder()
                        .data(Collections.emptyList())
                        .message("No notifications available")
                        .build();
            }
            List<NotificationResponse> notificationResponseList = notificationList.stream()
                    .map(notification -> notificationMapper.toResponse(notification))
                    .collect(Collectors.toList());
            return ApiResponse.<List<NotificationResponse>>builder()
                    .data(notificationResponseList)
                    .message("Notifications retrieved successfully")
                    .build();

        } catch (Exception e) {
            return ApiResponse.<List<NotificationResponse>>builder()
                    .data(Collections.emptyList())
                    .code(101)
                    .message("Failed to retrieve notifications: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<NotificationResponse>> getNotificationByUser(Pageable pageable) {
        try {
            String email = jwtProvider.getEmailContext();
            if (email == null || email.isEmpty()) {
                throw new AppException(ErrorCode.USER_NOT_FOUND);
            }
            Account account = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
            Shop shop = shopRepository.findByEmail(email);
            Long accId = account.getId();
            List<Notification> notificationList = notificationRepository.findByAccIdOrderByCreatedAtDesc(accId, pageable);
            if (notificationList.isEmpty()) {
                return ApiResponse.<List<NotificationResponse>>builder()
                        .data(Collections.emptyList())
                        .message("No notifications available for this user")
                        .build();
            }
            List<NotificationResponse> notificationResponseList = notificationList.stream()
                    .map(notification -> notificationMapper.toResponse(notification))
                    .collect(Collectors.toList());
            return ApiResponse.<List<NotificationResponse>>builder()
                    .data(notificationResponseList)
                    .message("Notifications retrieved successfully for user")
                    .build();

        } catch (AppException e) {
            return ApiResponse.<List<NotificationResponse>>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<NotificationResponse>>builder()
                    .code(500)
                    .message("Failed to retrieve notifications: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<NotificationResponse>> getNotificationByShop(Pageable pageable) {
        try {
            String email = jwtProvider.getEmailContext();
            if (email == null || email.isEmpty()) {
                throw new AppException(ErrorCode.USER_NOT_FOUND);
            }
            Account account = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
            Shop shop = shopRepository.findByEmail(email);
            Long accId = account.getId();
            List<Notification> notificationList = notificationRepository.findByAccIdOrderByCreatedAtDesc(shop.getId(), pageable);
            if (notificationList.isEmpty()) {
                return ApiResponse.<List<NotificationResponse>>builder()
                        .data(Collections.emptyList())
                        .message("No notifications available for this shop")
                        .build();
            }
            List<NotificationResponse> notificationResponseList = notificationList.stream()
                    .map(notification -> notificationMapper.toResponse(notification))
                    .collect(Collectors.toList());
            return ApiResponse.<List<NotificationResponse>>builder()
                    .data(notificationResponseList)
                    .message("Notifications retrieved successfully for shop")
                    .build();

        } catch (AppException e) {
            return ApiResponse.<List<NotificationResponse>>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<NotificationResponse>>builder()
                    .code(500)
                    .message("Failed to retrieve notifications: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<Void> markNotificationAsSeen(Long notificationId) {
        try {
            notificationRepository.updateNotificationStatus(notificationId, EnumNotificationStatus.seen);
            return ApiResponse.<Void>builder()
                    .message("Notification marked as seen.")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<Void>builder()
                    .code(500)
                    .message("Failed to update notification status: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<NotificationResponse>> getNotificationsByStatus(EnumNotificationStatus status, Pageable pageable) {
        try {
            String email = jwtProvider.getEmailContext();
            if (email == null || email.isEmpty()) {
                throw new AppException(ErrorCode.USER_NOT_FOUND);
            }
            Account account = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
            Long accId = account.getId();
            List<Notification> notifications = notificationRepository.findByAccIdAndStatusOrderByCreatedAtDesc(accId, status, pageable);
            if (notifications.isEmpty()) {
                return ApiResponse.<List<NotificationResponse>>builder()
                        .data(Collections.emptyList())
                        .message("No notifications found with the given status.")
                        .build();
            }
            List<NotificationResponse> notificationResponseList = notifications.stream()
                    .map(notificationMapper::toResponse)
                    .collect(Collectors.toList());
            return ApiResponse.<List<NotificationResponse>>builder()
                    .data(notificationResponseList)
                    .message("Notifications retrieved successfully.")
                    .build();
        } catch (AppException e) {
            return ApiResponse.<List<NotificationResponse>>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<NotificationResponse>>builder()
                    .code(500)
                    .message("Failed to retrieve notifications: " + e.getMessage())
                    .build();
        }
    }



}
