package org.example.cy_shop.service.shop_statics;

import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.response.product.ProductResponse;
import org.example.cy_shop.dto.response.shop.ShopResponse;
import org.example.cy_shop.dto.response.shop.shop_static.ShopInforResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IShopStaticService {
//    ShopResponse
    ApiResponse<ShopInforResponse> getShopInfo(Long idShop);
    ApiResponse<ShopInforResponse> getShopInfo();

    ApiResponse<Page<ProductResponse>>getMyProductShop(Long idShop, Long idCategory, Boolean newPrd, Boolean bestSeller, Pageable pageable);
}
