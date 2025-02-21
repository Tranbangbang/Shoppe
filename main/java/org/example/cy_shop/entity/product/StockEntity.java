package org.example.cy_shop.entity.product;

import jakarta.persistence.*;
import lombok.*;
import org.example.cy_shop.entity.BaseEntity;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_stock")
public class StockEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long quantity;
    private Double price;

    @Column(name = "version_update")
    private Long versionUpdate;

    @OneToMany(mappedBy = "stock")
    private List<ValueEntity> value;

    @ManyToOne
    @JoinColumn(name = "id_product")
    private ProductEntity product;
}
