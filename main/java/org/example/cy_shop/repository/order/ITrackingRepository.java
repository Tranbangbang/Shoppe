package org.example.cy_shop.repository.order;

import org.example.cy_shop.entity.order.TrackingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITrackingRepository extends JpaRepository<TrackingEntity, Long> {
    TrackingEntity findByOrderId(Long idOrder);
}
