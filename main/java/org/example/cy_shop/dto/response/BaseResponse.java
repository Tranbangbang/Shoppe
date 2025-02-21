package org.example.cy_shop.dto.response;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class BaseResponse {
    private LocalDateTime createDate;
    private LocalDateTime modifierDate;

    private String createBy;
    private String modifierBy;
}
