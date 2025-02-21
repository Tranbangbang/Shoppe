package org.example.cy_shop.service;

import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.response.SearchHistoryResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ISearchHistoryService {
    ApiResponse<List<SearchHistoryResponse>> listShop(Pageable pageable);
}
