package org.example.cy_shop.controller.search;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cy_shop.controller.client.product.ClientProductController;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.search.SearchResult;
import org.example.cy_shop.dto.response.SearchHistoryResponse;
import org.example.cy_shop.service.ISearchHistoryService;
import org.example.cy_shop.service.ISearchService;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "11. SEARCH")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/search")
public class SearchController {
    @Autowired
    private ISearchService searchService;

    @Autowired
    private ISearchHistoryService searchHistoryService;

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<SearchResult>> search(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "all") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        ApiResponse<SearchResult> response = searchService.search(pageable,keyword, type);
        return ResponseEntity.status(response.getCode()).body(response);
    }


    @GetMapping
    public ResponseEntity<ApiResponse<SearchResult>> search(
            @RequestParam String keyword,
            @RequestParam(required = false, defaultValue = "all") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(value = "place", required = false) String place,
            @RequestParam(value = "idCategory", required = false) Long idCategory,
            @RequestParam(value = "startPrice", required = false) String startPriceRaw,
            @RequestParam(value = "endPrice", required = false) String endPriceRaw,
            @RequestParam(value = "rating", required = false) Double rating
    ) {
        Pageable pageable = PageRequest.of(page, size);
        ApiResponse<SearchResult> response = searchService.search(pageable, keyword, type, place, idCategory, startPriceRaw, endPriceRaw, rating);
        return ResponseEntity.status(response.getCode()).body(response);
    }


    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<SearchHistoryResponse>>> getSearchHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        ApiResponse<List<SearchHistoryResponse>> response = searchHistoryService.listShop(pageable);
        return ResponseEntity.status(response.getCode()).body(response);
    }
}

//ClientProductController