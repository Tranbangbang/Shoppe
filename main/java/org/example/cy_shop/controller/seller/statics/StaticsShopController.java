package org.example.cy_shop.controller.seller.statics;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.response.product.ProductResponse;
import org.example.cy_shop.dto.response.shop.shop_static.ShopInforResponse;
import org.example.cy_shop.entity.BaseEntity;
import org.example.cy_shop.service.shop_statics.IShopStaticService;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "STATIC_SHOP_CONTROLLER. MANAGER_SHOP_STATICS")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/shop_statics")
public class StaticsShopController {
    @Autowired
    IShopStaticService shopStaticService;

    @Operation(
            summary = "Lấy thông tin cơ bản (shop - theo id)"
    )
    @GetMapping("/get_info")
    public ApiResponse<ShopInforResponse> getShopInfo(@RequestParam("idShop") Long idShop) {
        return shopStaticService.getShopInfo(idShop);
    }

    @Operation(
            summary = "Lấy thông tin cơ bản (shop của account login)"
    )
    @GetMapping("/get_my_info")
    public ApiResponse<ShopInforResponse> getMyShopInfo() {
        return shopStaticService.getShopInfo();
    }

    @Operation(
            summary = "Lấy danh sách sản phẩm",
            description = "Nếu không truyền id, thì là shop của mình. Nếu có, sẽ lấy theo id shop"
    )
    @GetMapping("/get_my_product")
    public ApiResponse<Page<ProductResponse>> getMyProductShop(@RequestParam(name = "page", required = false) Integer page,
                                                               @RequestParam(name = "limit", required = false) Integer limit,
                                                               @RequestParam(name = "idCategory", required = false) Long idCate,
                                                               @RequestParam(name = "newPrd", required = false) Boolean newPrd,
                                                               @RequestParam(name = "popularPrd", required = false) Boolean popularPrd,
                                                               @RequestParam(name = "idShop", required = false)Long idShop) {

        if(page == null)
            page = 1;
        if(limit == null)
            limit = 5;

        if(newPrd == null)
            newPrd = false;
        if(popularPrd == null)
            popularPrd = false;

        Pageable pageable = null;
        if(popularPrd == false){
            pageable = PageRequest.of(page-1, limit, Sort.by(Sort.Order.by("createDate")));
        }else if(popularPrd == true){
            pageable = PageRequest.of(page-1, limit);
        }

        return shopStaticService.getMyProductShop(idShop, idCate, newPrd, popularPrd, pageable);
    }
}


