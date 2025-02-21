package org.example.cy_shop.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "identityVerification")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private String fullName;
    private String identityNumber;
    private String idCardImagePath;
    private LocalDate birthDate;
    private String homeTown;
    private String gender;
    private Boolean isVerified = false;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
