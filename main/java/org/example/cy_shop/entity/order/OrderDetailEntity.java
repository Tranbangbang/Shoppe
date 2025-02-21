package org.example.cy_shop.entity.order;

import jakarta.persistence.*;
import lombok.*;
import org.example.cy_shop.entity.BaseEntity;
import org.example.cy_shop.entity.feedback.FeedBackEntity;
import org.example.cy_shop.entity.product.ProductEntity;
import org.example.cy_shop.mapper.feedback.FeedbackMapper;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_order_item")
public class OrderDetailEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String variant;
    private String image;
    private Long quantity;
    private Double price;
//    private String message;
    private Double lastPrice;

    @ManyToOne
    @JoinColumn(name = "id_order")
    private OrderEntity order;

    @ManyToOne
    @JoinColumn(name = "id_product")
    private ProductEntity product;

    @OneToMany(mappedBy = "orderDetail")
    private List<FeedBackEntity> feedBack;
}

//FeedbackMapper
