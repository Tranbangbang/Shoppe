package org.example.cy_shop.entity.order;

import jakarta.persistence.*;
import lombok.*;
import org.example.cy_shop.entity.BaseEntity;
import org.example.cy_shop.enums.order.StatusOrderEnum;
import org.example.cy_shop.enums.user.TypeUserEnum;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_tracking", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_order", "status"})
})
public class TrackingEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private StatusOrderEnum status;

    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_update_status")
    private TypeUserEnum userUpdateStatus;

    @ManyToOne
    @JoinColumn(name = "id_order")
    private OrderEntity order;
}
