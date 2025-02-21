package org.example.cy_shop.repository.product_repository;

import org.example.cy_shop.entity.product.ProductEntity;
import org.example.cy_shop.entity.product.ProductView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface IProductViewRepository extends JpaRepository<ProductView,Long> {

    boolean existsByProductAndUserIdAndViewedAtBetween(
            ProductEntity product, Long userId, LocalDateTime start, LocalDateTime end
    );

    boolean existsByProductAndUserIpAndViewedAtBetween(
            ProductEntity product, String userIp, LocalDateTime start, LocalDateTime end
    );

    Long countByProductAndViewedAtBetween(
            ProductEntity product, LocalDateTime start, LocalDateTime end
    );

    @Query("SELECT COUNT(pv) FROM ProductView pv WHERE pv.product.shop.id = :shopId AND pv.viewedAt BETWEEN :startDate AND :endDate")
    Long countViewsByShop(@Param("shopId") Long shopId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
