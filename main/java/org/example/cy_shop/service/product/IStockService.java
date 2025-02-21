package org.example.cy_shop.service.product;

import org.example.cy_shop.controller.order.client.ClientOrderController;
import org.example.cy_shop.controller.search.SearchController;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.order.VariantDTO;
import org.example.cy_shop.dto.request.product.add.ManyStockRequest;
import org.example.cy_shop.dto.request.product.add.StockRequest;
import org.example.cy_shop.dto.response.cart.StockInfo;
import org.example.cy_shop.dto.response.order.OrderShopResponse;
import org.example.cy_shop.dto.response.product.stock_response.StockResponse;
import org.example.cy_shop.entity.UserRole;
import org.example.cy_shop.service.impl.SearchServiceImpl;
import org.example.cy_shop.service.impl.feedback.FeedbackService;
import org.example.cy_shop.service.impl.order.OrderService;
import org.example.cy_shop.service.impl.shop_statics.ShopStaticsService;

import java.util.List;

public interface IStockService {
    StockResponse findById(Long id);
    List<StockResponse> findByProductId(Long productId);

    ApiResponse<StockResponse> save(StockRequest stockRequest, Long idProduct, Long vesionUpdate);
    ApiResponse<String> saveMany(ManyStockRequest manyStockRequest, Boolean update);

    ApiResponse<String>deleteMany( Long idProduct);
    ApiResponse<String>update(ManyStockRequest manyStockRequest);

    List<Long> findIdsStockByVariants(List<VariantDTO> variantDTOS, Long idProduct);
    StockInfo getStockInfo(List<VariantDTO> variantDTOS, Long idProduct);

    Long getAllQuantityByIdProduct(Long idPrd);
    Double getMinPriceByIdPrd(Long idPrd);
    Double getMaxPriceByIdPrd(Long idPrd);

}


//OrderShopResponse
//ClientOrderController