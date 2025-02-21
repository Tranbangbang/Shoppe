package org.example.cy_shop.repository.product_repository;

import org.example.cy_shop.entity.Shop;
import org.example.cy_shop.entity.product.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICategoryRepository extends JpaRepository<CategoryEntity, Long> {
    CategoryEntity findByName(String name);

    List<CategoryEntity> findByLevel(int i);
    List<CategoryEntity>findByIdParent(Long idParent);

    boolean existsById(Long id);

    @Query("SELECT s FROM CategoryEntity s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<CategoryEntity> searchByKeyword(@Param("keyword") String keyword);
}
