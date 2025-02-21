package org.example.cy_shop.entity.product;

import jakarta.persistence.*;
import lombok.*;
import org.example.cy_shop.entity.BaseEntity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_value", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"option_value", "id_option", "id_stock"})
})
public class ValueEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option_value", nullable = false)
    private String optionValue;

    @Column(name = "id_product")
    private Long idProduct;

    private String image;

    @Column(name = "version_update")
    private Long versionUpdate;

    @ManyToOne
    @JoinColumn(name = "id_option")
    private OptionEntity option;

    @ManyToOne
    @JoinColumn(name = "id_stock")
    private StockEntity stock;

}
