package org.example.cy_shop.repository.product_repository;

import org.example.cy_shop.entity.product.ValueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IValueRepository extends JpaRepository<ValueEntity, Long> {
    @Query("select vl from ValueEntity  vl where vl.optionValue = :name and vl.option.id = :idOption")
    ValueEntity findByNameAndIdOption(@Param("name") String name,
                                      @Param("idOption") Long idOption);

    @Query("select vl from ValueEntity  vl where vl.optionValue = :name and vl.option.id = :idOption and vl.stock.id = :idStock")
    ValueEntity findByNameAndIdOptionAndIdStock(@Param("name") String name,
                                                @Param("idOption") Long idOption,
                                                @Param("idStock") Long idStock);

    List<ValueEntity> findByIdProduct(Long idProduct);
    List<ValueEntity> findByOptionId(Long id);

    @Query("select vl from ValueEntity vl where vl.option.id = :id group by vl.optionValue")
    List<ValueEntity> findDistinctByOptionId(@Param("id") Long id);

    //vl.option.optionName = :option1 and
    @Query("select vl.stock.id from ValueEntity  vl where (:option1 is null  or :value1 is null or (vl.optionValue = :value1 and vl.option.optionName = :option1) ) and vl.idProduct = :idProduct")
    List<Long> getByOption(@Param("option1") String option1,
                                  @Param("value1") String value1,
                           @Param("idProduct") Long idProduct);

    @Query("select  vl from ValueEntity vl where vl.option.optionName = :optionName and vl.optionValue = :optionValue and " +
            " vl.idProduct = :idProduct")
    List<ValueEntity> existsByOptionNameAndOptionValueAndIdProduct(@Param("optionName") String optionName,
                                                         @Param("optionValue")String optionValue,
                                                         @Param("idProduct")Long idProduct);

    @Query("select vl from ValueEntity vl where vl.stock.id = :idStock ")
    ValueEntity findByIdStock(@Param("idStock") Long idStock);

    @Query("select vl from ValueEntity vl where vl.stock.id = :idStock ")
    List<ValueEntity> findManyByIdStock(@Param("idStock") Long idStock);
}
