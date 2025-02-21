package org.example.cy_shop.dto.request.product;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OptionRequest {
    private Long id;
    private String optionName;
    private Long idProduct;
    private Long versionUpdate;
}
