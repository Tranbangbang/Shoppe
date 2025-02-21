package org.example.cy_shop.dto.response.product;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.example.cy_shop.dto.response.BaseResponse;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CategoryResponse extends BaseResponse {
    private Long id;
    private String name;
    private int level;
    private String image;
    private Long idParent;
}
