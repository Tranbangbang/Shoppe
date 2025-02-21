package org.example.cy_shop.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.cy_shop.enums.EnumNotificationStatus;
import org.example.cy_shop.enums.EnumTypeStatus;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    // ID của actor
    @Column(name = "actor_id")
    private Long actorId;

    // Tên hiển thị của actor
    @Column(name = "actor_name")
    private String actorName;

    // ID của người nhận thông báo
    @Column(name = "user_id")
    private Long accId;

    // Username của người nhận
    @Column(name = "username")
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private EnumTypeStatus type;

    @Lob
    @Column(name = "content")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EnumNotificationStatus status;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "seen_at")
    private Timestamp seenAt;
}
