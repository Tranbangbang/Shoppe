package org.example.cy_shop.entity.feedback;

import jakarta.persistence.*;
import lombok.*;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.BaseEntity;
import org.example.cy_shop.entity.order.OrderDetailEntity;
import org.example.cy_shop.entity.order.OrderEntity;
import org.example.cy_shop.entity.product.ProductEntity;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_feed_back")
public class FeedBackEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String comment;

    @Column(nullable = false)
    private Double rating;

    @Column(name = "id_parent")
    private Integer idParent;

    @OneToMany(mappedBy = "feedBack")
    private List<MediaFeedBackEntity> media;

    @ManyToOne
    @JoinColumn(name = "id_order_detail")
    private OrderDetailEntity orderDetail;

//    @ManyToOne
//    @JoinColumn(name = "id_order")
//    private OrderEntity order;

//    @OneToOne
//    @JoinColumn(name = "id_product")
//    private ProductEntity product;

}
