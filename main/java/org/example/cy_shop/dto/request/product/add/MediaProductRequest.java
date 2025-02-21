package org.example.cy_shop.dto.request.product.add;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MediaProductRequest {
    private Long id;
    private String sourceMedia;
    private String typeMedia;
    private Long productId;
}
