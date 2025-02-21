package org.example.cy_shop.repository.feedback;

import org.example.cy_shop.dto.response.feedback.CountFeedbackResponse;
import org.example.cy_shop.entity.feedback.FeedBackEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFeedbackRepository extends JpaRepository<FeedBackEntity, Long> {

    @Query("select fb from FeedBackEntity fb where fb.orderDetail.product.id = :idPrd " +
            " and (:starVote is null or fb.rating = :starVote) " +
            " and (:isText is null or (:isText = true and fb.comment is not null) ) " +
            " and (:isMedia is null or (:isMedia = true and fb.media is not empty) )" +
            " and fb.orderDetail.order.account.isActive=true")
    Page<FeedBackEntity> findAll(@Param("idPrd") Long idPrd,
                                 @Param("starVote") Double star,
                                 @Param("isText") Boolean isText,
                                 @Param("isMedia") Boolean isMedia,
                                 Pageable pageable);

    @Query("select fb from FeedBackEntity fb where fb.orderDetail.product.id = :idPrd and fb.orderDetail.order.account.isActive=true")
    List<FeedBackEntity> findByIdProduct(@Param("idPrd") Long idPrd);

    @Query("SELECT new org.example.cy_shop.dto.response.feedback.CountFeedbackResponse( " +
            "  COUNT(CASE WHEN fb.rating = 5 THEN 1 END), " +
            "  COUNT(CASE WHEN fb.rating = 4 THEN 1 END), " +
            "  COUNT(CASE WHEN fb.rating = 3 THEN 1 END), " +
            "  COUNT(CASE WHEN fb.rating = 2 THEN 1 END), " +
            "  COUNT(CASE WHEN fb.rating = 1 THEN 1 END), " +
            "  COUNT(CASE WHEN fb.comment is not null THEN 1 END), " +
            "  COUNT(CASE WHEN fb.media is not empty THEN 1 END) ) " +
            "FROM FeedBackEntity fb " +
            "WHERE fb.orderDetail.product.id = :idPrd " +
            "AND fb.orderDetail.order.account.isActive = true")
    CountFeedbackResponse countFeedbacksByProductId(@Param("idPrd") Long idPrd);

}
