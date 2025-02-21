package org.example.cy_shop.repository.product_repository;

import org.example.cy_shop.entity.product.MediaProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMediaProductRepository extends JpaRepository<MediaProductEntity, Long> {
    List<MediaProductEntity> findByProductId(Long productId);

    Long id(Long id);
}
