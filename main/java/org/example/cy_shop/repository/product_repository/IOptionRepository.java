package org.example.cy_shop.repository.product_repository;

import org.example.cy_shop.entity.product.OptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IOptionRepository extends JpaRepository<OptionEntity, Long> {
    @Query("select op from OptionEntity  op where op.optionName = :name and op.idProduct = :idProduct")
    OptionEntity findByNameAndIdProduct(@Param("name") String name,
                                        @Param("idProduct") Long idProduct);

    boolean existsById(Long id);
    boolean existsByOptionNameAndIdProduct(String name, Long idProduct);

    List<OptionEntity> findByIdProduct(Long idProduct);
}
