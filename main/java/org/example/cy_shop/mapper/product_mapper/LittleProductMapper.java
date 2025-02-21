package org.example.cy_shop.mapper.product_mapper;

import org.example.cy_shop.controller.seller.SellerProductController;
import org.example.cy_shop.dto.response.feedback.CountFeedbackResponse;
import org.example.cy_shop.dto.response.product.LittleProductResponse;
import org.example.cy_shop.dto.response.product.MediaProductResponse;
import org.example.cy_shop.dto.response.product.ProductResponse;
import org.example.cy_shop.dto.response.shop.shop_static.ShopInforResponse;
import org.example.cy_shop.entity.product.ProductEntity;
import org.example.cy_shop.enums.TypeMediaEnum;
import org.example.cy_shop.enums.order.StatusOrderEnum;
import org.example.cy_shop.service.feedback.IFeedbackService;
import org.example.cy_shop.service.product.IMediaProductService;
import org.example.cy_shop.service.product.IStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LittleProductMapper {
    @Autowired
    IStockService stockService;
    @Autowired
    IMediaProductService mediaProductService;
    @Autowired
    IFeedbackService feedbackService;

    public LittleProductResponse convertToResposne(ProductEntity productEntity) {
        Long quantity = stockService.getAllQuantityByIdProduct(productEntity.getId());

        String coverImageSrc = null;
        String introViseoSrc = null;

        List<MediaProductResponse> mediaProductResponseList = mediaProductService.findByProductId(productEntity.getId());
        List<String> coverImage = mediaProductService.getSrcOfTypeMedia(mediaProductResponseList, TypeMediaEnum.COVER_IMAGE.name());
//            List<String> detailImage = mediaProductService.getSrcOfTypeMedia(mediaProductResponseList, TypeMediaEnum.DETAIL_IMAGE.name());
//            List<String> introVideo = mediaProductService.getSrcOfTypeMedia(mediaProductResponseList, TypeMediaEnum.INTRO_VIDEO.name());

        if (coverImage != null && coverImage.size() != 0)
            coverImageSrc = coverImage.get(0);
//            if(introVideo!= null && introVideo.size() != 0)
//                introViseoSrc = introVideo.get(0);

        List<String> imageProduct = new ArrayList<>();

        Double ratingAverage = null;
        try {
            ratingAverage = feedbackService.getRatingAve(productEntity.getId());
        } catch (Exception e) {
            System.out.println("Khong lay duoc rating (product mapper): " + e);
        }

        CountFeedbackResponse countFb = null;
        try {
            countFb = feedbackService.findCountByIdProduct(productEntity.getId());
        } catch (Exception e) {
            System.out.println("Khong the lay feedback count(product mapper): " + e);
        }
        Long cntSeller = 0L;
        try {
            if (productEntity != null && productEntity.getOrderDetail() != null) {
                for (var it : productEntity.getOrderDetail()) {
                    if (it.getOrder().getStatusOrder().name().equalsIgnoreCase(StatusOrderEnum.RECEIVED.name())) {
                        cntSeller += it.getQuantity();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Khong the lay so luong ban(product mapper) " + e);
        }

        return LittleProductResponse.builder()
                .id(productEntity.getId())
                .productName(productEntity.getProductName())
                .productDescription(productEntity.getProductDescription())

                .coverImage(coverImageSrc)


                .categoryId(productEntity.getCategory().getId())
                .categoryName(productEntity.getCategory().getName())


                .allQuantity(quantity)
                .minPrice(stockService.getMinPriceByIdPrd(productEntity.getId()))
                .maxPrice(stockService.getMaxPriceByIdPrd(productEntity.getId()))

                .ratingAverage(ratingAverage)
                .countFb(countFb)
                .countSeller(cntSeller)

                .build();

    }
}

//ShopInforResponse
//SellerProductController
//ShopInforResponse