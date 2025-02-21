package org.example.cy_shop.entity.product;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_product_view")
public class ProductView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "user_id", nullable = true)
    private Long userId; // Nếu người dùng chưa đăng nhập, có thể null

    @Column(name = "acc_ip")
    private String userIp;

    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt;
}
