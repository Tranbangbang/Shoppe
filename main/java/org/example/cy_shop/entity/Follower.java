package org.example.cy_shop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "followers")
public class Follower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 100)
    @Column(name = "username")
    private String username;

    @Size(max = 100)
    @Column(name = "followed_username")
    private String followedUsername;

    private Boolean is_active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
//neu da ton tai thi set is_active ve false.