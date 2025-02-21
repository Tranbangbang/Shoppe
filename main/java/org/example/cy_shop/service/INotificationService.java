package org.example.cy_shop.service;

import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.response.notification.NotificationResponse;
import org.example.cy_shop.enums.EnumNotificationStatus;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface INotificationService {
    ApiResponse<List<NotificationResponse>> getNotification();
    ApiResponse<List<NotificationResponse>> getNotificationByUser(Pageable pageable);
    ApiResponse<List<NotificationResponse>> getNotificationByShop(Pageable pageable);
    ApiResponse<Void> markNotificationAsSeen(Long notificationId);

    ApiResponse<List<NotificationResponse>> getNotificationsByStatus(EnumNotificationStatus status, Pageable pageable);
}
