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
@Table(name = "tbl_option", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"option_name", "id_product"})
})
public class OptionEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option_name", nullable = false)
    private String optionName;

    @Column(name = "id_product")
    private Long idProduct;

    @Column(name = "version_update")
    private Long versionUpdate;

    @OneToMany(mappedBy = "option")
    private List<ValueEntity> value;
}
