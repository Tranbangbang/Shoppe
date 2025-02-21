package org.example.cy_shop.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchHistoryResponse {
    private Long id;
    private Long accId;
    private String keyword;
}
