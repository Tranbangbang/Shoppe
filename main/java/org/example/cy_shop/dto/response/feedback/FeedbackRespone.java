package org.example.cy_shop.dto.response.feedback;

import lombok.*;
import org.example.cy_shop.dto.request.order.VariantDTO;
import org.example.cy_shop.dto.response.product.ProductResponse;
import org.example.cy_shop.mapper.feedback.FeedbackMapper;
import org.example.cy_shop.mapper.product_mapper.ProductMapper;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedbackRespone {
    private Long id;
    private String comment;
    private Double rating;
    private List<String>images;
    private String video;

    private Long idAcc;
    private String userName;
    private String avatar;
    private String shopName;

    private List<VariantDTO> variants;
    private LocalDateTime timeFeed;



//    private int idParent;
//    FeedbackMapper
}

//ProductMapper
