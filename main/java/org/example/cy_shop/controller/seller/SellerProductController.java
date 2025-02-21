package org.example.cy_shop.controller.seller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cy_shop.configuration.jwtConfig.JwtAuthenticationFilter;
import org.example.cy_shop.configuration.jwtConfig.JwtProvider;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.product.delete.ProductKeyRequest;
import org.example.cy_shop.dto.request.product.add.ProductAddRequest;
import org.example.cy_shop.dto.request.product.delete.ProductKeysRequest;
import org.example.cy_shop.dto.request.product.edit.ProductEditRequest;
import org.example.cy_shop.dto.request.search.SearchProduct;
import org.example.cy_shop.dto.response.cart.StockInfo;
import org.example.cy_shop.dto.response.product.*;
import org.example.cy_shop.dto.response.product.stock_response.LittleOptionResponse;
import org.example.cy_shop.entity.BaseEntity;
import org.example.cy_shop.entity.product.ProductEntity;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.repository.product_repository.IProductRepository;
import org.example.cy_shop.repository.product_repository.IValueRepository;
import org.example.cy_shop.service.impl.ShopServiceImpl;
import org.example.cy_shop.service.impl.order.OrderSchedule;
import org.example.cy_shop.service.impl.order.OrderService;
import org.example.cy_shop.service.impl.product.OptionService;
import org.example.cy_shop.service.impl.product.ProductService;
import org.example.cy_shop.service.product.IMediaProductService;
import org.example.cy_shop.service.product.IValueService;
import org.example.cy_shop.utils.Const;
import org.example.cy_shop.utils.UtilsFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "SELLER_01. MANAGER_PRODUCT(SELLER)")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/manager_product")
public class SellerProductController {
    @Autowired
    IMediaProductService imageProductService;
    @Autowired
    private ProductService productService;
    @Autowired
    JwtAuthenticationFilter authenticationFilter;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    ShopServiceImpl shopServiceImpl;
    @Autowired
    IValueService valueService;
    @Autowired
    IProductRepository productRepository;
    @Autowired
    IValueRepository valueRepository;

    @Value("${jwt.SECRET_ACCESS_TOKEN_KEY}")
    private String JWT_SECRET_ACCESS_TOKEN;
    @Autowired
    private OptionService optionService;


    //---thêm sản pẩm
    @Operation(
            summary = "Thêm simple sản phẩm",
            description = "Thêm phần ảnh, mô tả, tên,... cho sản phẩm"
    )
    @PreAuthorize("hasAnyRole('ROLE_SHOP')")
    @PostMapping("/add")
    public ApiResponse<Long> addProduct(@ModelAttribute ProductAddRequest productRequest) {
//        System.out.println("In the dunction ");
        String email = null;
        try {
            email = jwtProvider.getEmailContext();
            if (email == null)
                System.out.println("null");
            else
                System.out.println("email = " + email);
        } catch (Exception e) {
            System.out.println("Email null: " + e);
            throw new AppException(ErrorCode.USER_NOT_LOGIN);
        }


        var shop = shopServiceImpl.findByEmail(email);
        if (shop == null)
            throw new AppException(ErrorCode.USER_NOT_HAS_ROLE_SHOP);

        productRequest.setShopId(shop.getId());
//        System.out.println("id_shop: " + productRequest.getShopId());


        return productService.save(productRequest);
    }

    @GetMapping("/find_by_id")
    public ProductResponse findById(@RequestParam(value = "productId") Long productId) {
        return productService.findById(productId);
    }

    @Operation(
            summary = "Thông tin sản phẩm edit (chi tiết)",
            description = "Thông tin sản phẩm (gồm cả mô tả, và biến thể)"
    )
    @GetMapping("/product_detail_edit")
    public ProductDetailForEditResponse find(@RequestParam("idProduct") Long productId) {
        return productService.findByIdForEdit(productId);
    }

    //---edit
    @Operation(
            summary = "Chỉnh sửa sản phẩm (gộp)",
            description = "**Chỉnh sửa sản phẩm, (bao gồm thông tin sản phẩm và biến thể)**"

    )
    @PreAuthorize("hasAnyRole('ROLE_SHOP')")
    @PostMapping("/edit_product")
    public ApiResponse<String> update(@RequestBody ProductEditRequest productEditRequest) {
        String email = null;
        try {
            email = jwtProvider.getEmailContext();
            if (email == null)
                System.out.println("null");
            else
                System.out.println("email = " + email);
        } catch (Exception e) {
            System.out.println("Email null: " + e);
            throw new AppException(ErrorCode.USER_NOT_LOGIN);
        }


        var shop = shopServiceImpl.findByEmail(email);
        if (shop == null)
            throw new AppException(ErrorCode.USER_NOT_HAS_ROLE_SHOP);

        return productService.updateOneRequest(productEditRequest);
    }


    //---ấy ra option
    @GetMapping("/option_list")
    public ApiResponse<List<LittleOptionResponse>> getOptionUnique(@RequestParam("productId") Long productId) {
        try {
            List<LittleOptionResponse> result = optionService.findDistinceByProductId(productId);
            return new ApiResponse<>(200, "Danh sách option của sản phẩm", result);
        } catch (Exception e) {
//            String message = e.getMessage();
            return new ApiResponse<>(400, "Không thể lấy option của sản phẩm", null);
        }
    }

    //---lấy ra stock info
    @Operation(
            summary = "Lấy ra thông số lượng, giá, ảnh",
            description = "**Lấy ra số lượng, giá và ảnh của sản phẩm theo biến thể**"
    )
    @GetMapping("/quantity_price_of_product")
    public ApiResponse<StockInfo> getStockOfProduct(@RequestParam(name = "option1", required = false) String option1,
                                                    @RequestParam(name = "value1", required = false) String value1,
                                                    @RequestParam(name = "option2", required = false) String option2,
                                                    @RequestParam(name = "value2", required = false) String value2,
                                                    @RequestParam(name = "idProduct", required = false) Long idProduct) {


        option1 = UtilsFunction.convertParamToString(option1);
        value1 = UtilsFunction.convertParamToString(value1);
        option2 = UtilsFunction.convertParamToString(option2);
        value2 = UtilsFunction.convertParamToString(value2);

        if (idProduct == null)
            return new ApiResponse<>(400, "Id sản phẩm trống", null);

        return valueService.getStockByVariantAndIdProducts(option1, value1, option2, value2, idProduct);

    }

    //---hidden sản phẩm
    @PostMapping("/hidden_product")
    public ApiResponse<String> hidden(@RequestBody ProductKeyRequest productKeyRequest) {
        return productService.hidden(productKeyRequest);
    }

    @PostMapping("/hidden_many_product")
    public ApiResponse<String> hiddenMany(@RequestBody ProductKeysRequest productKeysRequest) {
        for (var it : productKeysRequest.getIdProducts()) {
            productService.hidden(it);
        }
        return new ApiResponse<>(200, "Ẩn sản phẩm thành công", null);
    }

    //---active
    @PostMapping("/active_product")
    public ApiResponse<String> active(@RequestBody ProductKeyRequest productKeyRequest) {
        return productService.active(productKeyRequest);
    }

    //---xóa
    @PostMapping("/delete_product")
    public ApiResponse<String> delete(@RequestBody ProductKeyRequest productKeyRequest) {
        return productService.delete(productKeyRequest);
    }

    @PostMapping("/delete_many_product")
    public ApiResponse<String> deleteMany(@RequestBody ProductKeysRequest productKeysRequest) {
        for (var it : productKeysRequest.getIdProducts()) {
            productService.delete(it);
        }
        return new ApiResponse<>(200, "Xóa sản phẩm thành công", null);
    }

    //---sản phẩm shop
    @GetMapping("/list_product_shop")
    @Operation(
            summary = "list sản phẩm (trang shop)",
            description = "**Chủ shop mới thấy được thông tin này**"
    )
    public ApiResponse<Page<ProductDetailForEditResponse>> getProductOfShop(@RequestParam(value = "page", required = false) Integer page,
                                                                            @RequestParam(value = "limit", required = false) Integer limit,
                                                                            @RequestParam(value = "keySearch", required = false) String keySearch,
                                                                            @RequestParam(value = "categoryName", required = false) String categoryName,
                                                                            @RequestParam(value = "place", required = false) String place,
                                                                            @RequestParam(value = "startPrice", required = false) String startPriceRaw,
                                                                            @RequestParam(value = "endPrice", required = false) String endPriceRaw,
                                                                            @RequestParam(value = "status", required = false)String status) {
        try {
            //--------------kiểm tra xem có phải quyền shop không-----
            String email = null;
            try {
                email = jwtProvider.getEmailContext();
                if (email == null)
                    System.out.println("null");
                else
                    System.out.println("email = " + email);
            } catch (Exception e) {
                System.out.println("Email null: " + e);
                return new ApiResponse<>(ErrorCode.USER_NOT_LOGIN.getCode(), ErrorCode.USER_NOT_LOGIN.getMessage(), null);
            }


            var shop = shopServiceImpl.findByEmail(email);
            if (shop == null)
                return new ApiResponse<>(ErrorCode.USER_NOT_HAS_ROLE_SHOP.getCode(), ErrorCode.USER_NOT_HAS_ROLE_SHOP.getMessage(), null);
            if (shop.getIsApproved() == false)
                return new ApiResponse<>(ErrorCode.SHOP_IS_NOT_ACTIVE.getCode(), ErrorCode.SHOP_IS_NOT_ACTIVE.getMessage(), null);

            Double startPrice = UtilsFunction.convertParamToDouble(startPriceRaw);
            Double endPrice = UtilsFunction.convertParamToDouble(endPriceRaw);

            SearchProduct searchProduct = SearchProduct.builder()
                    .page(page)
                    .limit(limit)
                    .keySearch(keySearch)
                    .place(place)
                    .shopId(shop.getId())
//                    .idSubCategory(idSubCategory)
//                    .idParentCategory(idParentCategory)
                    .startPrice(startPrice)
                    .endPrice(endPrice)
                    .status(status)
                    .build();
            searchProduct.simpleValide();
//            BaseEntity

            Pageable pageable = PageRequest.of(searchProduct.getPage() - 1, searchProduct.getLimit(), Sort.by(Sort.Order.desc("createDate")));
            Page<ProductDetailForEditResponse> productResponsePage = productService.findAllSpecCustomForShop(searchProduct, pageable);

            return new ApiResponse<>(200, "Danh sách sản phẩm", productResponsePage);
        } catch (NumberFormatException e) {
            return new ApiResponse<>(ErrorCode.PRICE_NOT_VALID.getCode(), ErrorCode.PRICE_NOT_VALID.getMessage(), null);
        } catch (Exception e) {
            return new ApiResponse<>(400, "Không thể tìm kiếm sản phẩm", null);
        }

    }
}


//ProductEntity
//OrderService
//OrderSchedule