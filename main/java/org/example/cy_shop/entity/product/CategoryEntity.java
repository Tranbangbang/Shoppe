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
@Table(name = "tbl_category",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class CategoryEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "id_parent")
    private Long idParent;

    private int level;

    private String image;

    @OneToMany(mappedBy = "category")
    private List<ProductEntity> product;
}
