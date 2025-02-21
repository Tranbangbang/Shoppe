package org.example.cy_shop.mapper.product_mapper;

import org.example.cy_shop.dto.request.product.add.MediaProductRequest;
import org.example.cy_shop.dto.response.product.MediaProductResponse;
import org.example.cy_shop.entity.product.MediaProductEntity;
import org.example.cy_shop.entity.product.ProductEntity;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.repository.product_repository.IProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MediaProductMapper {
    @Autowired
    IProductRepository productRepository;

    public MediaProductResponse convertToResponse(MediaProductEntity mediaProductEntity){
        return MediaProductResponse.builder()
                .id(mediaProductEntity.getId())
                .sourceMedia(mediaProductEntity.getSourceMedia())
                .typeMedia(mediaProductEntity.getTypeMedia())
                .productId(mediaProductEntity.getProduct().getId())
                .build();
    }

    public MediaProductEntity convertToEntity(MediaProductRequest mediaProductRequest){
        ProductEntity productEntity = new ProductEntity();
        Long productId = null;

        if(mediaProductRequest == null || mediaProductRequest.getProductId() == null)
            throw new AppException(ErrorCode.PRODUCT_OF_MEDIA_NULL);

        productId = mediaProductRequest.getProductId();
        if(productRepository.findById(productId) == null)
            throw new AppException(ErrorCode.PRODUCT_OF_MEDIA_INVALID);
        else
            productEntity = productRepository.findById(productId).orElse(null);

        return MediaProductEntity.builder()
                .sourceMedia(mediaProductRequest.getSourceMedia())
                .typeMedia(mediaProductRequest.getTypeMedia())
                .product(productEntity)
                .build();
    }
}
