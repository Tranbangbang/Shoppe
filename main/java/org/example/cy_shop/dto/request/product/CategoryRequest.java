package org.example.cy_shop.dto.request.product;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryRequest {
    private Long id;
    private String name;
    private int level;
    private String image;
    private Long idParent;

    public void preProcessing(){
        if(name != null)
            name = name.trim();
    }
}
