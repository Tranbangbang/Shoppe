package org.example.cy_shop.repository.feedback;

import org.example.cy_shop.entity.feedback.MediaFeedBackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IMediaFeedbackRepository extends JpaRepository<MediaFeedBackEntity, Long> {

    @Query("select md from MediaFeedBackEntity md where md.feedBack.id = :idFeed")
    List<MediaFeedBackEntity> findByFeedBackId(@Param("idFeed") Long idFeed);
}
