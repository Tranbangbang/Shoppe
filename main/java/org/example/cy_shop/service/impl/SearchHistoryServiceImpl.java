package org.example.cy_shop.service.impl;

import org.example.cy_shop.configuration.jwtConfig.JwtProvider;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.response.SearchHistoryResponse;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.SearchHistory;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.SearchHistoryMapper;
import org.example.cy_shop.repository.IAccountRepository;
import org.example.cy_shop.repository.ISearchHistoryRepository;
import org.example.cy_shop.service.ISearchHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchHistoryServiceImpl implements ISearchHistoryService {
    @Autowired
    private ISearchHistoryRepository searchHistoryRepository;
    @Autowired
    private IAccountRepository accountRepository;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private SearchHistoryMapper searchHistoryMapper;
    @Override
    public ApiResponse<List<SearchHistoryResponse>> listShop(Pageable pageable) {
        try{
            String email = jwtProvider.getEmailContext();
            if (email == null || email.isEmpty()) {
                throw new AppException(ErrorCode.USER_NOT_FOUND);
            }
            Account reporter = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
            List<SearchHistory> searchHistories = searchHistoryRepository.findByAccIdOrderByCreatedAtDesc(reporter.getId(), pageable);
            List<SearchHistoryResponse> responseList = searchHistories.stream()
                    .map(searchHistoryMapper::toResponse)
                    .toList();
            return ApiResponse.<List<SearchHistoryResponse>>builder()
                    .code(200)
                    .message("Search history fetched successfully.")
                    .data(responseList)
                    .build();
        }catch (Exception e) {
            return ApiResponse.<List<SearchHistoryResponse>>builder()
                    .code(500)
                    .message("Error fetching search history: " + e.getMessage())
                    .build();
        }
    }
}
