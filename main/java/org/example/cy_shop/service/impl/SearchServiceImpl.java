package org.example.cy_shop.service.impl;

import org.example.cy_shop.configuration.jwtConfig.JwtProvider;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.search.SearchProduct;
import org.example.cy_shop.dto.request.search.SearchResult;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.SearchHistory;
import org.example.cy_shop.entity.Shop;
import org.example.cy_shop.entity.product.ProductEntity;
import org.example.cy_shop.mapper.ShopMapper;
import org.example.cy_shop.mapper.product_mapper.CategoryMapper;
import org.example.cy_shop.mapper.product_mapper.ProductMapper;
import org.example.cy_shop.repository.IAccountRepository;
import org.example.cy_shop.repository.ISearchHistoryRepository;
import org.example.cy_shop.repository.IShopRepository;
import org.example.cy_shop.repository.product_repository.ICategoryRepository;
import org.example.cy_shop.repository.product_repository.IProductRepository;
import org.example.cy_shop.service.ISearchService;
import org.example.cy_shop.service.impl.product.ProductService;
import org.example.cy_shop.specification.ProductSpecification;
import org.example.cy_shop.utils.UtilsFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SearchServiceImpl implements ISearchService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private IProductRepository productRepository;
    @Autowired
    private ProductSpecification productSpecification;

    @Autowired
    private IShopRepository shopRepository;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private ICategoryRepository categoryRepository;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private IAccountRepository accountRepository;
    @Autowired
    private ISearchHistoryRepository searchHistoryRepository;
    @Autowired
    private ProductService productService;

    @Override
    public ApiResponse<SearchResult> search(Pageable pageable, String keyword, String type) {
        try {
            String redisKey = "search:" + keyword + ":" + type;
            Object cachedResult = redisTemplate.opsForValue().get(redisKey);
            if (cachedResult != null) {
                return ApiResponse.<SearchResult>builder()
                        .code(200)
                        .data((SearchResult) cachedResult)
                        .message("Search result fetched from cache.")
                        .build();
            }
            String email = null;
            try {
                email = jwtProvider.getEmailContext();
            } catch (Exception jwtException) {
                email = null;
            }
            Optional<Account> accountOptional = email != null ? accountRepository.findByEmail(email) : Optional.empty();

            Account account = accountOptional.orElse(null);

            LocalDateTime vietNamTimeNow = UtilsFunction.getVietNameTimeNow();
            SearchHistory searchHistory = new SearchHistory();
            searchHistory.setAccId(account != null ? account.getId() : null);
            searchHistory.setKeyword(keyword);
            searchHistory.setCreatedAt(vietNamTimeNow);

            searchHistoryRepository.save(searchHistory);


            SearchResult searchResult = new SearchResult();
            if ("product".equalsIgnoreCase(type)) {
                searchResult.setType("product");
                searchResult.setData(productRepository.searchByKeywordAndFilters(keyword, pageable)
                        .stream()
                        .map(productMapper::toResponse)
                        .toList());
//                searchResult.setData( productService.findAllSpecCustom(searchProduct, pageable));
            } else if ("shop".equalsIgnoreCase(type)) {
                searchResult.setType("shop");
                searchResult.setData(shopRepository.searchByKeyword(keyword, pageable)
                        .stream()
                        .map(shopMapper::toResponse)
                        .toList());
            } else {
                // Tìm kiếm cả sản phẩm và shop
                searchResult.setType("all");
                Map<String, Object> allResults = new HashMap<>();
                allResults.put("products", productRepository.searchByKeywordAndFilters(keyword, pageable)
                        .stream()
                        .map(productMapper::toResponse)
                        .toList());
//                allResults.put("products", productService.findAllSpecCustom(searchProduct, pageable));
                allResults.put("shops", shopRepository.searchByKeyword(keyword, pageable)
                        .stream()
                        .map(shopMapper::toResponse)
                        .toList());
                /*
                allResults.put("category", categoryRepository.searchByKeyword(keyword)
                        .stream()
                        .map(categoryMapper::convertToResponse)
                        .toList());

                 */
                searchResult.setData(allResults);
            }

            // Lưu vào Redis cache
            redisTemplate.opsForValue().set(redisKey, searchResult, Duration.ofMinutes(5));

            return ApiResponse.<SearchResult>builder()
                    .code(200)
                    .data(searchResult)
                    .message("Search result fetched successfully.")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<SearchResult>builder()
                    .code(500)
                    .message("Error performing search: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<SearchResult> search(Pageable pageable, String keyword, String type, String place, Long idCategory, String startPriceRaw, String endPriceRaw, Double rating) {
        try {
            Double startPrice = UtilsFunction.convertParamToDouble(startPriceRaw);
            Double endPrice = UtilsFunction.convertParamToDouble(endPriceRaw);

            String redisKey = String.format("search:%s:%s:%s:%s:%s:%s:%s", keyword, type, place, idCategory, startPrice, endPrice, rating);
            Object cachedResult = redisTemplate.opsForValue().get(redisKey);

            if (cachedResult != null) {
                return ApiResponse.<SearchResult>builder()
                        .code(200)
                        .data((SearchResult) cachedResult)
                        .message("Search result fetched from cache.")
                        .build();
            }

            String email = null;
            try {
                email = jwtProvider.getEmailContext();
            } catch (Exception jwtException) {
                email = null;
            }
            Optional<Account> accountOptional = email != null ? accountRepository.findByEmail(email) : Optional.empty();
            Account account = accountOptional.orElse(null);

            if (account != null) {
                LocalDateTime vietNamTimeNow = UtilsFunction.getVietNameTimeNow();
                SearchHistory searchHistory = new SearchHistory();
                searchHistory.setAccId(account.getId());
                searchHistory.setKeyword(keyword);
                searchHistory.setCreatedAt(vietNamTimeNow);
                searchHistoryRepository.save(searchHistory);
            }

            // Thực hiện tìm kiếm sản phẩm
            Page<ProductEntity> productPage = productRepository.searchByKeywordAndFilterss(keyword, place, idCategory, startPrice, endPrice, rating, pageable);
            List<Object> products = Collections.singletonList(productPage.stream()
                    .map(productMapper::toResponse)
                    .toList());

            // Thực hiện tìm kiếm shop
            Page<Shop> shopPage = shopRepository.searchByKeyword(keyword, pageable);
            List<Object> shops = Collections.singletonList(shopPage.stream()
                    .map(shopMapper::toResponse)
                    .toList());

            SearchResult searchResult = new SearchResult();
            searchResult.setType("all");

            Map<String, Object> allResults = new HashMap<>();
            allResults.put("products", products);
            allResults.put("shops", shops);

            searchResult.setData(allResults);

            // Tổng số phần tử và trang từ productPage và shopPage
            searchResult.setTotalElements(productPage.getTotalElements() + shopPage.getTotalElements());
            searchResult.setTotalPages(Math.max(productPage.getTotalPages(), shopPage.getTotalPages()));

            // Lưu vào Redis cache
            redisTemplate.opsForValue().set(redisKey, searchResult, Duration.ofMinutes(5));

            return ApiResponse.<SearchResult>builder()
                    .code(200)
                    .data(searchResult)
                    .message("Search result fetched successfully.")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<SearchResult>builder()
                    .code(500)
                    .message("Error performing search: " + e.getMessage())
                    .build();
        }
    }



//    @Override
//    public ApiResponse<SearchResult> searchProduct(SearchProduct searchProduct, Pageable pageable) {
//        String redisKey = "search:" + searchProduct.getKeySearch();
//        Object cachedResult = redisTemplate.opsForValue().get(redisKey);
//        if (cachedResult != null) {
//            return ApiResponse.<SearchResult>builder()
//                    .code(200)
//                    .data((SearchResult) cachedResult)
//                    .message("Search result fetched from cache.")
//                    .build();

//            return new ApiResponse<>(200, "Danh sách product", (Page< ProductResponse >) cachedResult);
//        }
//        return null;
//    }


    public Specification<ProductEntity> buildSpecification(SearchProduct searchProduct) {
        return Specification
                .where(productSpecification.isActive(searchProduct.getActive()))
                .and(productSpecification.hasKeySearch(searchProduct.getKeySearch()))
                .and(productSpecification.hasStartPrice(searchProduct.getStartPrice()))
                .and(productSpecification.hasEndPrice(searchProduct.getEndPrice()))
                .and(productSpecification.hasSubCategoryId(searchProduct.getIdSubCategory()))
                .and(productSpecification.hasParentCategoryId(searchProduct.getIdParentCategory()))
                .and(productSpecification.hasCategoryId(searchProduct.getIdCategory()))
                .and(productSpecification.defaultFind());
    }


}
