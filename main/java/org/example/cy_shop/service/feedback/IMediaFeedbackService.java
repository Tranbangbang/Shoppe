package org.example.cy_shop.service.feedback;

import org.example.cy_shop.entity.feedback.MediaFeedBackEntity;

import java.util.List;

public interface IMediaFeedbackService {
    List<MediaFeedBackEntity> findByIdFeedback(Long idFeed);
}
