package org.example.cy_shop.dto.request.feedback;

import lombok.*;
import org.example.cy_shop.dto.request.order.VariantDTO;
import org.example.cy_shop.entity.feedback.FeedBackEntity;
import org.example.cy_shop.utils.UtilsFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class FeedbackRequest {
    private Long idOrderDetail;

    private String comment;
    private Double rating;

    private List<String>images;
    private String videos;

    public void simpleValidate(){
        this.videos = UtilsFunction.convertParamToString(this.videos);
        for(var it: images){
            it = UtilsFunction.convertParamToString(it);
        }
    }
}

