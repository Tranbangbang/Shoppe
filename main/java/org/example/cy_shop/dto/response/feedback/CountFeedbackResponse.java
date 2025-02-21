package org.example.cy_shop.dto.response.feedback;

import lombok.*;
import org.example.cy_shop.mapper.feedback.FeedbackMapper;
import org.example.cy_shop.service.impl.feedback.FeedbackService;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CountFeedbackResponse {
    private Long count5Star;
    private Long count4Star;
    private Long count3Star;
    private Long count2tar;
    private Long count1Star;

    private Long countHasText;
    private Long countHasMedia;
}

