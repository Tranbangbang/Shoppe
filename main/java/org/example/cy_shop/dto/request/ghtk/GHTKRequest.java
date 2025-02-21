package org.example.cy_shop.dto.request.ghtk;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GHTKRequest {
    private String province;
    private String district;
    private String pick_province;
    private String pick_district;
    private Double weight = 1.0;
    private Long value;
    private String deliver_option = "xteam";
}
