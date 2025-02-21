package org.example.cy_shop.repository;

import org.example.cy_shop.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IBannerRepository extends JpaRepository<Banner, Long> {
        Banner findByOrderNumber(Integer orderNumber);
        List<Banner> findAllByOrderByOrderNumberAsc();
        List<Banner> findByOrderNumberBetween(Integer startNumber, Integer endNumber);
        List<Banner> findByOrderNumberGreaterThan(Integer orderNumber);
        Banner getBannerById(Long id);
}
