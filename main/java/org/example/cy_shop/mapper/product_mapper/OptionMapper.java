package org.example.cy_shop.mapper.product_mapper;

import org.example.cy_shop.dto.request.product.OptionRequest;
import org.example.cy_shop.dto.response.product.stock_response.LittleOptionResponse;
import org.example.cy_shop.dto.response.product.stock_response.OptionResponse;
import org.example.cy_shop.dto.response.product.ProductResponse;
import org.example.cy_shop.dto.response.product.stock_response.ValueResponse;
import org.example.cy_shop.entity.product.OptionEntity;
import org.example.cy_shop.entity.product.ValueEntity;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.repository.product_repository.IValueRepository;
import org.example.cy_shop.service.impl.product.ProductService;
import org.example.cy_shop.service.product.IValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OptionMapper {
    @Autowired
    ProductService productService;
    @Autowired
    IValueService valueService;
    @Autowired
    IValueRepository valueRepository;

    public OptionResponse convertToResponse(OptionEntity optionEntity){
        List<ValueResponse> valueResponses = new ArrayList<>();
        if(optionEntity != null && optionEntity.getId() != null){
            List<ValueResponse> valueResponsesFind = valueService.findByOptionId(optionEntity.getId());
            if(valueResponsesFind != null)
                valueResponses = valueResponsesFind;
        }

        return OptionResponse.builder()
                .id(optionEntity.getId())
                .optionName(optionEntity.getOptionName())
                .idProduct(optionEntity.getIdProduct())
                .valueResponseList(valueResponses)
                .build();
    }

    public OptionEntity convertToEntity(OptionRequest optionRequest){
        if(optionRequest == null || optionRequest.getIdProduct() == null)
            throw new AppException(ErrorCode.PRODUCT_OF_OPTION_NULL);

        ProductResponse productResponse = productService.findById(optionRequest.getIdProduct());
        if(productResponse == null)
            throw new AppException(ErrorCode.PRODUCT_OF_OPTION_NOT_FOUND);

        return OptionEntity.builder()
                .optionName(optionRequest.getOptionName())
                .idProduct(optionRequest.getIdProduct())
                .build();
    }


    //------------------custome
    public LittleOptionResponse convertToLittleOption(OptionEntity optionEntity){
        List<String> optionValue = new ArrayList<>();

        if(optionEntity != null && optionEntity.getId() != null) {
            List<ValueEntity> valueEntityList = valueRepository.findDistinctByOptionId(optionEntity.getId());
            for (var it: valueEntityList)
                optionValue.add(it.getOptionValue());
        }

        return LittleOptionResponse.builder()
                .optionName(optionEntity.getOptionName())
                .optionValue(optionValue)
                .build();
    }
}
