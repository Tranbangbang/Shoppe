package org.example.cy_shop.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.cy_shop.entity.Notification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
        private final SimpMessagingTemplate simpMessagingTemplate;

        public void senNotification(String userId, Notification notification){
            log.info("Sending ws notification to {} with payload {}", userId,notification);
            simpMessagingTemplate.convertAndSendToUser(
                     userId,
                    "/notifications",
                    notification
            );
        }
}
