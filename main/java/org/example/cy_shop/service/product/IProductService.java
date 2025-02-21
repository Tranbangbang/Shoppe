package org.example.cy_shop.service.product;

import org.example.cy_shop.controller.order.client.ClientCartController;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.product.delete.ProductKeyRequest;
import org.example.cy_shop.dto.request.product.add.ProductAddRequest;
import org.example.cy_shop.dto.request.product.delete.ProductKeysRequest;
import org.example.cy_shop.dto.request.product.edit.ProductEditRequest;
import org.example.cy_shop.dto.request.search.SearchProduct;
import org.example.cy_shop.dto.response.product.LittleProductResponse;
import org.example.cy_shop.dto.response.product.ProductDetailForEditResponse;
import org.example.cy_shop.dto.response.product.ProductRecommendationResponse;
import org.example.cy_shop.dto.response.product.ProductResponse;
import org.example.cy_shop.entity.Shop;
import org.example.cy_shop.entity.product.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IProductService {
    ProductDetailForEditResponse findByIdForEdit(Long idProduct);
    ProductResponse findById(Long id);

//    Page<ProductResponse> getBestSeller(Pageable pageable);
    Page<LittleProductResponse> getBestSeller(Pageable pageable);

//    Page<ProductResponse> getAllNews(Pageable pageable);
    Page<LittleProductResponse> getAllNews(Pageable pageable);

    //-------------------tìm kiếm cho
    Page<ProductResponse>findAllCustom (SearchProduct searchProduct, Pageable pageable);
    Page<ProductResponse> findAllSpecCustom(SearchProduct searchProduct, Pageable pageable);
    Page<ProductDetailForEditResponse> findAllSpecCustomForShop(SearchProduct searchProduct, Pageable pageable);

    Boolean checkExits(Long id);

    Long getQuantityOfProduct(String op1, String vl1, String op2, String vl2);

    ApiResponse<Long> save(ProductAddRequest request);
    ApiResponse<String> update(ProductAddRequest request);
    ApiResponse<String> updateOneRequest(ProductEditRequest productEditRequest);

    //---ẩn sản phẩm
    ApiResponse<String>hidden(ProductKeyRequest productKeyRequest);
    ApiResponse<String>active(ProductKeyRequest productKeyRequest);

    //---xóa sản phẩm
    ApiResponse<String> delete(ProductKeyRequest productKeysRequest);


    //---list sản phẩm bị ban
    List<Long> getAllReport(Long idShop, LocalDate start, LocalDate end);
    List<ProductEntity> getAllOutStock(Long idShop);

    void updateCart(Long idProduct, Boolean updateVariant, Boolean updateStatus);
    void updateOrder(Long idProduct, Boolean updateVariant, Boolean updateStatus);
    void updateCartShop(Long idShop);
    void updateOrderShop(Long idShop);
//    List<ProductEntity> ge(Long idShop, LocalDate start, LocalDate end);
//    List<ProductE>
//    ClientCartController


    List<ProductRecommendationResponse> getRecommendations(String keyword);
}
