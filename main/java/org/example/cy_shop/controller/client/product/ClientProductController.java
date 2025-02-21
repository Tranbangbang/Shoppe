package org.example.cy_shop.controller.client.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.search.SearchProduct;
import org.example.cy_shop.dto.response.product.LittleProductResponse;
import org.example.cy_shop.dto.response.product.ProductRecommendationResponse;
import org.example.cy_shop.dto.response.product.ProductResponse;
import org.example.cy_shop.dto.response.shop.shop_static.ShopInforResponse;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.service.IProductViewService;
import org.example.cy_shop.service.product.IProductService;
import org.example.cy_shop.utils.Const;
import org.example.cy_shop.utils.UtilsFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "CLIENT_01. PRODUCT(VIEW)")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/client_product")
public class ClientProductController {
    @Autowired
    IProductService productService;
    @Autowired
    private IProductViewService productViewService;
    @GetMapping("/find")
    ApiResponse<Page<ProductResponse>> find(@RequestParam(value = "page", required = false) Integer page,
                                            @RequestParam(value = "limit", required = false) Integer limit,
                                            @RequestParam(value = "keySearch", required = false) String keySearch,
//                                             @RequestParam(value = "categoryName", required = false) String categoryName,
                                            @RequestParam(value = "place", required = false) String place,
//                                            @RequestParam(value = "places", required = false) List<String>places,
//                                            @RequestParam(value = "idSubCategory", required = false) Long idSubCategory,
//                                            @RequestParam(value = "idParentCategory", required = false) Long idParentCategory,
                                            @RequestParam(value = "idCategory", required = false) Long idCategory,
//                                            @RequestParam(value = "idCategories", required = false) List<Long>idCategories,
                                            @RequestParam(value = "startPrice", required = false) String startPriceRaw,
                                            @RequestParam(value = "endPrice", required = false) String endPriceRaw,
                                            @RequestParam(value = "sortBy", required = false) String sortBy,
                                            @RequestParam(value = "rating", required = false) Double rating) {

        try {
            Double startPrice = UtilsFunction.convertParamToDouble(startPriceRaw);
            Double endPrice = UtilsFunction.convertParamToDouble(endPriceRaw);

            SearchProduct searchProduct = SearchProduct.builder()
                    .page(page)
                    .limit(limit)
                    .keySearch(keySearch)
                    .place(place)
                    .idCategory(idCategory)
                    .startPrice(startPrice)
                    .endPrice(endPrice)
                    .sortBy(sortBy)
                    .rating(rating)
                    .build();

            searchProduct.simpleValide();

            Pageable pageable = PageRequest.of(searchProduct.getPage() - 1, searchProduct.getLimit());
            Page<ProductResponse> productResponsePage = productService.findAllSpecCustom(searchProduct, pageable);

            return new ApiResponse<>(200, "Danh sách sản phẩm", productResponsePage);
        } catch (NumberFormatException e) {
            return new ApiResponse<>(ErrorCode.PRICE_NOT_VALID.getCode(), ErrorCode.PRICE_NOT_VALID.getMessage(), null);
        } catch (Exception e) {
            String mess = "Không thể tìm kiếm sản phẩm" + e.getMessage();
            return new ApiResponse<>(400, mess, null);
        }
    }

    @GetMapping("/find_best_seller")
    @Operation(
            summary = "Sản phẩm bán chạy nhất",
            description = "**Danh sách sản phẩm bán chạy nhất**"
    )
    ApiResponse<Page<LittleProductResponse>> getBestSeller(@RequestParam(value = "page", required = false) Integer page,
                                                     @RequestParam(value = "limit", required = false) Integer limit) {
        if (page == null)
            page = 1;
        if (limit == null)
            limit = 20;

        Pageable pageable = PageRequest.of(page - 1, limit);
        return new ApiResponse<>(200, "Best seller", productService.getBestSeller(pageable));
    }

    @GetMapping("/find_recomend")
    @Operation(
            summary = "Sản phẩm recommend",
            description = "**Sản phẩm gợi ý (recommend)**"
    )
//    ProductResponse
    ApiResponse<Page<LittleProductResponse>> getRecommend(@RequestParam(value = "page", required = false) Integer page,
                                                          @RequestParam(value = "limit", required = false) Integer limit) {
        try {
            if (page == null)
                page = 1;
            if (limit == null)
                limit = 20;

            Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Order.desc("createDate")));
            var result = productService.getAllNews(pageable);

            return new ApiResponse<>(200, "Recomment", result);
        } catch (Exception e) {
            System.out.println("Loi list recommend: " + e);
            return null;
        }
    }

    @PostMapping("/{productId}/view")
    public ResponseEntity<ApiResponse<Long>> recordProductView(
            @PathVariable Long productId,
            HttpServletRequest request
    ) {
        return productViewService.recordProductView(productId, request);
    }


    @GetMapping("/recommendations")
    public ResponseEntity<?> getRecommendedProducts(@RequestParam String keyword) {
        List<ProductRecommendationResponse> recommendations = productService.getRecommendations(keyword);
        return ResponseEntity.ok().body(
                ApiResponse.<List<ProductRecommendationResponse>>builder()
                        .message("Recommendations fetched successfully")
                        .data(recommendations)
                        .build()
        );
    }


//    Account

}

//ShopInforResponse