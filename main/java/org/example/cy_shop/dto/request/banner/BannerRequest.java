package org.example.cy_shop.dto.request.banner;


import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerRequest {
    private Long id;
    private String name;
    private Integer orderNumber;
    private MultipartFile image;
}
