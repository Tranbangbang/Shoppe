package org.example.cy_shop.dto.response.notification;

import lombok.*;
import org.example.cy_shop.enums.EnumNotificationStatus;
import org.example.cy_shop.enums.EnumTypeStatus;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private Long actorId;
    private Long accId;
    private String username;
    private String actorName;

    private EnumTypeStatus type;
    private String content;
    private EnumNotificationStatus status;
    private Timestamp createdAt;
    private Timestamp seenAt;
}
