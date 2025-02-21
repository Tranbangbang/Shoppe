package org.example.cy_shop.service.product;

import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.product.OptionRequest;
import org.example.cy_shop.dto.request.product.edit.ImageOptionRequest;
import org.example.cy_shop.dto.response.product.stock_response.LittleOptionResponse;
import org.example.cy_shop.dto.response.product.stock_response.OneParentOptionResponse;
import org.example.cy_shop.dto.response.product.stock_response.OptionResponse;

import java.util.List;

public interface IOptionService {
    String getImageOption(String optionName, String optionValue, Long idProduct);
    List<LittleOptionResponse> findDistinceByProductId(Long productId);
    List<OptionResponse> findByProductId(Long id);
    OptionResponse findByNameAndProductId(String name, Long productId);

    List<OneParentOptionResponse> findListParentResponse(Long productId);


    ApiResponse<OptionResponse> save(OptionRequest optionRequest);
}
