package org.example.cy_shop.mapper;

import org.example.cy_shop.dto.response.SearchHistoryResponse;
import org.example.cy_shop.entity.SearchHistory;
import org.springframework.stereotype.Component;

@Component
public class SearchHistoryMapper {
    public SearchHistoryResponse toResponse(SearchHistory searchHistory){
        return SearchHistoryResponse.builder()
                .id(searchHistory.getId())
                .accId(searchHistory.getAccId())
                .keyword(searchHistory.getKeyword())
                .build();
    }
}
