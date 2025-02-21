package org.example.cy_shop.dto.response.product;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MediaProductResponse {
    private Long id;
    private String sourceMedia;
    private String typeMedia;
    private Long productId;
}
