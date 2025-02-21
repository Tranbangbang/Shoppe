package org.example.cy_shop.mapper.product_mapper;

import org.example.cy_shop.dto.request.product.add.StockRequest;
import org.example.cy_shop.dto.response.product.stock_response.StockResponse;
import org.example.cy_shop.entity.product.ProductEntity;
import org.example.cy_shop.entity.product.StockEntity;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.repository.product_repository.IProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StockMapper {
    @Autowired
    IProductRepository productRepository;

    public StockResponse convertToResponse(StockEntity stockEntity){
        if(stockEntity == null || stockEntity.getProduct() == null || stockEntity.getProduct().getId() == null)
            throw new AppException(ErrorCode.STOCK_INVALID);

        return StockResponse.builder()
                .id(stockEntity.getId())
                .quantity(stockEntity.getQuantity())
                .price(stockEntity.getPrice())

                .idProduct(stockEntity.getProduct().getId())
                .productName(stockEntity.getProduct().getProductName())

                .createDate(stockEntity.getCreateDate())
                .modifierDate(stockEntity.getModifierDate())
                .createBy(stockEntity.getCreateBy())
                .modifierBy(stockEntity.getModifierBy())

                .build();
    }

    public StockEntity convertToStockEntity(StockRequest stockRequest){
        ProductEntity productEntity = new ProductEntity();

        if(stockRequest.getQuantity() == null || stockRequest.getQuantity() < 0 || stockRequest.getPrice() == null || stockRequest.getPrice() < 0)
            throw new AppException(ErrorCode.STOCK_INVALID);

//        boolean hasProduct = productRepository.existsById(stockRequest.getIdProduct());
//        if(hasProduct == false){
//            throw new AppException(ErrorCode.PRODUCT_OF_STOCK_REQUEST_NOT_FOUND);
//        }else {
//            productEntity.setId(stockRequest.getIdProduct());
//        }

        return StockEntity.builder()
                .quantity(stockRequest.getQuantity())
                .price(stockRequest.getPrice())
//                .product(productEntity)
//                .versionUpdate(stockRequest.getVersionUpdate())
                .build();
    }

}
