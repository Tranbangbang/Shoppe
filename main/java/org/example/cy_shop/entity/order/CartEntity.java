package org.example.cy_shop.entity.order;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.BaseEntity;
import org.example.cy_shop.enums.StatusCartEnum;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_cart")
public class CartEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long quantity;
//    private Double price;

    private String variant;

//    private String image;

    @NotNull
    @Enumerated(EnumType.STRING)
    private StatusCartEnum status;

    @Column(name = "id_product")
    private Long productId;

    @ManyToOne
    @JoinColumn(name = "id_account")
    private Account account;

}
