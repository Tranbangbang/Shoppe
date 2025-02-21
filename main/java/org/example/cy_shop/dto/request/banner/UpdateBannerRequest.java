package org.example.cy_shop.dto.request.banner;


import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBannerRequest {
    private Long id;
    private String name;
    private Integer newOrderNumber;
    private String image;
}
