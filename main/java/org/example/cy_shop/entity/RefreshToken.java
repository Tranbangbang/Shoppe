package org.example.cy_shop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "refresh_tokens")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "token", columnDefinition = "TEXT")
    private String token;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "iat") // ngày tạo
    private LocalDateTime iat;

    @Column(name = "exp") // ngày hết hạn
    private LocalDateTime exp;

    @Column(name = "device_info", columnDefinition = "TEXT")
    private String deviceInfo;
}