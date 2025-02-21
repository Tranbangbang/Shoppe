package org.example.cy_shop.mapper;

import org.example.cy_shop.dto.request.notification.NotificationRequest;
import org.example.cy_shop.dto.response.notification.NotificationResponse;
import org.example.cy_shop.entity.Notification;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class NotificationMapper {
    public Notification toEntity(NotificationRequest notificationRequest){
        return Notification.builder()
                .id(notificationRequest.getId())
                .accId(notificationRequest.getAccId())
                .actorId(notificationRequest.getActorId())
                .username(notificationRequest.getUsername())
                .actorName(notificationRequest.getActorName())
                .type(notificationRequest.getType())
                .status(notificationRequest.getStatus())
                .content(notificationRequest.getContent())
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .seenAt(notificationRequest.getSeenAt())
                .build();
    }

    public NotificationResponse toResponse(Notification notification){
        return NotificationResponse.builder()
                .id(notification.getId())
                .actorId(notification.getActorId())
                .accId(notification.getAccId())
                .username(notification.getUsername())
                .actorName(notification.getActorName())
                .type(notification.getType())
                .status(notification.getStatus())
                .content(notification.getContent())
                .createdAt(notification.getCreatedAt())
                .seenAt(notification.getSeenAt())
                .build();
    }
}
