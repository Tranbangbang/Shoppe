package org.example.cy_shop.repository;

import org.example.cy_shop.entity.Banner;
import org.example.cy_shop.entity.PromotionalBanner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPromotionalBannerRepository extends JpaRepository<PromotionalBanner,Long> {
    PromotionalBanner getPromotionalBannerById(Long id);
}
