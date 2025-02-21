package org.example.cy_shop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import jakarta.persistence.*;
import org.example.cy_shop.entity.order.OrderDetailEntity;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_vouchers")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVoucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "acc_id", referencedColumnName = "id", nullable = false)
    private Account account;
    @ManyToOne
    @JoinColumn(name = "voucher_id", referencedColumnName = "id", nullable = false)
    private Voucher voucher;
    @Column(name = "used_at")
    private LocalDateTime usedAt;
    @Column(name = "is_valid")
    private Boolean isValid = true;


}
