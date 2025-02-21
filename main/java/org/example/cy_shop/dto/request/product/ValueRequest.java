package org.example.cy_shop.dto.request.product;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValueRequest {
    private Long id;
    private String optionValue;
    private Long idOption;
    private Long idStock;
    private Long versionUpdate;

    public void simpleValidate(){
        if(this.optionValue != null)
            this.optionValue = this.optionValue.trim();
    }
}
