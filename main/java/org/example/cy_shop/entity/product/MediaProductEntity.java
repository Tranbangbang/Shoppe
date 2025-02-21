package org.example.cy_shop.entity.product;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_media_product")
public class MediaProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_media")
    private String sourceMedia;

    @Column(name = "type_media")
    private String typeMedia;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;
}
