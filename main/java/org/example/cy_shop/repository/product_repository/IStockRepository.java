package org.example.cy_shop.repository.product_repository;

import org.example.cy_shop.entity.product.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IStockRepository extends JpaRepository<StockEntity, Long> {
    List<StockEntity> findByProductId(Long productId);

    boolean existsById(Long id);
    @Query("select st from StockEntity  st where st.product.id = :productId")
    List<StockEntity> findByIdProduct(@Param("productId") Long productId);

    @Query("select sum(st.quantity) from StockEntity  st where st.id in :ids")
    Long getQuantity(@Param("ids") List<Long> ids);

    @Query("select sum(st.quantity) from StockEntity st where st.product.id = :idPrd")
    Long getQuantityByIdPrd(@Param("idPrd") Long idPrd);

    @Query("select min (st.price) from StockEntity st where st.product.id = :idPrd")
    Double getMinPriceByIdPrd(@Param("idPrd") Long idPrd);

    @Query("select max (st.price) from StockEntity st where st.product.id = :idPrd")
    Double getMaxPriceByIdPrd(@Param("idPrd") Long idPrd);

    @Query("select sum(st.price) from StockEntity  st where st.id in :ids")
    Double getPrice(@Param("ids") List<Long> ids);

}
