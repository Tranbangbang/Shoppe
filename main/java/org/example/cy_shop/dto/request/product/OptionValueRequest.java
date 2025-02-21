package org.example.cy_shop.dto.request.product;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OptionValueRequest {
    private String optionName;
    private String optionValue;

    public void simpleValidate(){
        if(this.optionName != null)
            this.optionName = this.optionName.trim();

        if(this.optionValue != null)
            this.optionValue = this.optionValue.trim();
    }

    @Override
    public String toString() {
        return "OptionValueRequest{" +
                "optionName='" + optionName + '\'' +
                ", optionValue='" + optionValue + '\'' +
                '}';
    }
}
