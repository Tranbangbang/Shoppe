package org.example.cy_shop.mapper.product_mapper;

import org.example.cy_shop.dto.response.product.stock_response.ValueResponse;
import org.example.cy_shop.entity.product.OptionEntity;
import org.example.cy_shop.entity.product.StockEntity;
import org.example.cy_shop.entity.product.ValueEntity;
import org.example.cy_shop.entity.product.ValueRequest;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.repository.product_repository.IOptionRepository;
import org.example.cy_shop.repository.product_repository.IStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValueMapper {
    @Autowired
    IOptionRepository optionRepository;
    @Autowired
    IStockRepository stockRepository;

    public ValueResponse convertToResponse(ValueEntity valueEntity){
        Long quantity = null;
        Double price = null;

        if(valueEntity != null && valueEntity.getStock()!= null && valueEntity.getStock().getId() != null){
            StockEntity stockEntity = stockRepository.findById(valueEntity.getStock().getId()).orElse(null);
            if(stockEntity != null){
                quantity = stockEntity.getQuantity();
                price = stockEntity.getPrice();
            }
        }

        return ValueResponse.builder()
                .id(valueEntity.getId())
                .optionValue(valueEntity.getOptionValue())

                .idOption(valueEntity.getOption().getId())
                .optionName(valueEntity.getOption().getOptionName())

                .idStock(valueEntity.getStock().getId())

                .quantity(quantity)
                .price(price)
                .build();
    }

    public ValueEntity convertToEntity(ValueRequest valueRequest){
        OptionEntity optionEntity = new OptionEntity();
        StockEntity stockEntity = new StockEntity();

        if(valueRequest == null || valueRequest.getIdOption() == null || valueRequest.getIdStock() == null)
            throw new AppException(ErrorCode.VALUE_OF_PRODUCT_NOT_VALID);


        boolean isExitOption = optionRepository.existsById(valueRequest.getIdOption());
        if(isExitOption == false){
            throw new AppException(ErrorCode.OPTION_OF_VALUE_NOT_FOUND);
        }else
            optionEntity.setId(valueRequest.getIdOption());


        boolean isExitStock = stockRepository.existsById(valueRequest.getIdStock());
        if(isExitStock == false)
            throw new AppException(ErrorCode.STOCK_OF_VALUE_NOT_FOUND);
        else
            stockEntity.setId(valueRequest.getIdStock());

        return ValueEntity.builder()
                .optionValue(valueRequest.getOptionValue())
                .option(optionEntity)
                .stock(stockEntity)

                .idProduct(valueRequest.getIdProduct())
                .versionUpdate(valueRequest.getVersionUpdate())
                .build();
    }
}
