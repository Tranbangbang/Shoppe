package org.example.cy_shop.repository;

import org.example.cy_shop.entity.Notification;
import org.example.cy_shop.enums.EnumNotificationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface INotificationRepository extends JpaRepository<Notification,Long> {
    List<Notification> findByAccIdOrderByCreatedAtDesc(Long id, Pageable pageable);
    @Query("SELECT n FROM Notification n WHERE (n.accId = :accId OR n.actorId = :shopId) ORDER BY n.createdAt DESC")
    List<Notification> findByUserOrShop(@Param("accId") Long accId, @Param("shopId") Long shopId, Pageable pageable);

    List<Notification> findByAccIdAndStatusOrderByCreatedAtDesc(Long accId, EnumNotificationStatus status, Pageable pageable);


    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.status = :status WHERE n.id = :notificationId")
    void updateNotificationStatus(@Param("notificationId") Long notificationId, @Param("status") EnumNotificationStatus status);
}
