package org.example.cy_shop.service.feedback;

import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.feedback.FeedbackRequest;
import org.example.cy_shop.dto.response.feedback.CountFeedbackResponse;
import org.example.cy_shop.dto.response.feedback.FeedbackRespone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IFeedbackService {
    Page<FeedbackRespone> findAll(Long idProduct, String fileterBy, Pageable pageable);

    ApiResponse<Long> add(List<FeedbackRequest> requests);

    ApiResponse<String> delete(Long id);

    Double getRatingAve(Long idPrd);

    CountFeedbackResponse findCountByIdProduct(Long idPrd);
}
