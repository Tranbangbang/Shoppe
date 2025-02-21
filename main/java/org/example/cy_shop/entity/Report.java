package org.example.cy_shop.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.example.cy_shop.entity.product.ProductEntity;
import org.example.cy_shop.enums.EnumReportStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account reporter;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    private Long shop_id;

    @Size(max = 500)
    @Column(name = "reason", insertable = false, updatable = false)
    private String reason;

    @Size(max = 500)
    @Column(name = "reason")
    private String desc;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EnumReportStatus status ;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
