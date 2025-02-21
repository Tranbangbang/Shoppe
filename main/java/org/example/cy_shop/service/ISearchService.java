package org.example.cy_shop.service;

import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.search.SearchProduct;
import org.example.cy_shop.dto.request.search.SearchResult;
import org.springframework.data.domain.Pageable;

public interface ISearchService {
    ApiResponse<SearchResult> search(Pageable pageable, String keyword, String type);
//    ApiResponse<SearchResult> searchProduct(SearchProduct searchProduct, Pageable pageable);


    ApiResponse<SearchResult> search(Pageable pageable, String keyword, String type, String place, Long idCategory, String startPriceRaw, String endPriceRaw, Double rating);
}
