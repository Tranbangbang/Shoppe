package org.example.cy_shop.dto.request.search;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchResult {
    private String type;
    private Object data;
    private long totalElements;
    private int totalPages;
}
