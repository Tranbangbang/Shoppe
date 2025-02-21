package org.example.cy_shop.service.impl.feedback;

import jakarta.transaction.Transactional;
import org.example.cy_shop.entity.feedback.MediaFeedBackEntity;
import org.example.cy_shop.repository.feedback.IMediaFeedbackRepository;
import org.example.cy_shop.service.feedback.IMediaFeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class MediaFeedbackService implements IMediaFeedbackService {
    @Autowired
    IMediaFeedbackRepository mediaFeedbackRepository;

    @Override
    public List<MediaFeedBackEntity> findByIdFeedback(Long idFeed) {
        return mediaFeedbackRepository.findByFeedBackId(idFeed);
    }
}
