package org.example.cy_shop.dto.response.product;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductForEditResponse {
    private Long id;
    private String name;
    private String doanhSo;
    private Long quantity;
//    private Doubleprice;
    private Double rating;
}
