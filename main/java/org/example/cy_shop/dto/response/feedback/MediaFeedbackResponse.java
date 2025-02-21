package org.example.cy_shop.dto.response.feedback;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MediaFeedbackResponse {
    private Long id;
    private List<String> images;
    private String video;
}
