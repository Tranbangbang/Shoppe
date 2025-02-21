package org.example.cy_shop.dto.response.banner;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerResponse {
    private Long id;
    private String name;
    private Integer orderNumber;
    private String image;
    private LocalDateTime createdAt;
    //private LocalDateTime updatedAt;
}
