package org.example.cy_shop.dto.response.ghtk;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeeResponse {
    private Double fee;
    private Double distance;
}
