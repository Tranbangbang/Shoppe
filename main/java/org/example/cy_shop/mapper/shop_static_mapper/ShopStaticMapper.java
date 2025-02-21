package org.example.cy_shop.mapper.shop_static_mapper;

import org.example.cy_shop.dto.response.shop.shop_static.CategoryInfoResponse;
import org.example.cy_shop.dto.response.shop.shop_static.ShopInforResponse;
import org.example.cy_shop.entity.Shop;
import org.example.cy_shop.enums.order.StatusOrderEnum;
import org.example.cy_shop.service.IUserVoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ShopStaticMapper {
    @Autowired
    IUserVoucherService userVoucherService;

    public ShopInforResponse convertToShopInforResponse(Shop shop) {
        if (shop == null)
            return null;

        String avatar = null;
        try {
            avatar = shop.getAccount().getUser().getAvatar();
        } catch (Exception e) {
            System.out.println("Loi lay avatar shop(shop static mapper): " + e);
        }
        Long cntProduct = (long) shop.getProduct().size();
        Long cntFeedback = 0L;
        try {
            for (var prd : shop.getProduct()) {
                for (var ordet : prd.getOrderDetail()) {
                    if (ordet.getFeedBack() != null) {
                        cntFeedback++;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Loi dem so feed back(shop static mapper): " + e);
        }

        Long cntSeller = 0L;
        try {
            for (var prd : shop.getProduct()) {
                if (prd != null && prd.getOrderDetail() != null) {
                    for (var it : prd.getOrderDetail()) {
                        if (it.getOrder().getStatusOrder().name().equalsIgnoreCase(StatusOrderEnum.RECEIVED.name())) {
                            cntSeller += it.getQuantity();
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Khong the lay so luong ban(shop static mapper): " + e);
        }

        List<CategoryInfoResponse> category = new ArrayList<>();
        Map<Long, String> cateMap = new TreeMap<>();
        try {
            for(var it: shop.getProduct()){
                cateMap.put(it.getCategory().getId(), it.getCategory().getName());
            }
            cateMap.forEach((id, name) -> category.add(new CategoryInfoResponse(id, name)));
        }catch (Exception e){
            System.out.println("Loi khi lay category cua shop");
        }

        return ShopInforResponse.builder()
                .id(shop.getId())
                .shopName(shop.getShopName())
                .avatar(avatar)
                .dateParticipation(shop.getCreatedAt())
                .countProduct(cntProduct)
                .countFeedback(cntFeedback)
                .cntSeller(cntSeller)
                .category(category)
                .build();
    }
}

//StatusOrderEnum