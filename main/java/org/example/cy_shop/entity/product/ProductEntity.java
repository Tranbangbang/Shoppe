package org.example.cy_shop.entity.product;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.example.cy_shop.controller.search.SearchController;
import org.example.cy_shop.entity.BaseEntity;
import org.example.cy_shop.entity.Report;
import org.example.cy_shop.entity.Shop;
import org.example.cy_shop.entity.order.OrderDetailEntity;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_product")
public class ProductEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_code", unique = true)
    private String productCode;

    @Column(name = "description", columnDefinition = "TEXT")
    private String productDescription;

    @Column(name = "version_update")
    private Long versionUpdate;

//    @NotNull
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "is_ban", nullable = false)
    private Boolean isBan;

//    @NotNull
    @Column(name = "is_delete", nullable = false)
    private Boolean isDelete;


    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MediaProductEntity> mediaProductEntity;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<StockEntity> stock;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Report> reports;

    @OneToMany(mappedBy = "product")
    private List<OrderDetailEntity> orderDetail;

}
//SearchController