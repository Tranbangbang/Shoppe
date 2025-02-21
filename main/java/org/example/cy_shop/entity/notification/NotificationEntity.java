package org.example.cy_shop.entity.notification;

import jakarta.persistence.*;
import lombok.*;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.BaseEntity;
import org.example.cy_shop.enums.StatusEnum;
import org.example.cy_shop.enums.TypeNotificationEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_notification")
public class NotificationEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_notity")
    private TypeNotificationEnum typeNotify;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusEnum status;

    private String message;
    private String link;

    @ManyToOne
    @JoinColumn(name = "id_account")
    private Account account;
}
