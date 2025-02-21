package org.example.cy_shop.service.product;

import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.product.edit.ImageOptionRequest;
import org.example.cy_shop.dto.response.cart.StockInfo;
import org.example.cy_shop.dto.response.product.stock_response.ValueResponse;
import org.example.cy_shop.entity.product.ValueEntity;
import org.example.cy_shop.entity.product.ValueRequest;

import java.util.List;

public interface IValueService {
    ValueResponse findByNameAndIdOption(String value, Long idOption);
    ValueResponse findByValueAndIdOptionAndIdStock(String value, Long idOption, Long idStock);
    List<ValueResponse> findByOptionId(Long optionId);
    List<ValueResponse> findDistinceByOptionId(Long id);

    List<Long> findIdStockByManyOptionAndValue(String option1, String value1, String option2, String value2, Long idProduct);
    Long getQuantityByManyOptionAndValue(String option1, String value1, String option2, String value2, Long idProduct);
    Double getPriceByManyOptionAndValue(String option1, String value1, String option2, String value2, Long idProduct );
//    String getImageByManyOptionValue(String op1, String vl1, String op2, String vl2, Long idProduct);

    ApiResponse<ValueResponse> save(ValueRequest valueRequest);

    //------cập nhật ảnh cho option
    ApiResponse<String> updateValueImage(ImageOptionRequest imageOptionRequest, Long idProduct);

    ApiResponse<StockInfo> getStockByVariantAndIdProducts(String op1, String vl1, String op2, String vl2, Long idPrd);
}
