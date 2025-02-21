package org.example.cy_shop.service.impl.product;

import org.example.cy_shop.controller.seller.SellerStockController;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.product.edit.ImageOptionRequest;
import org.example.cy_shop.dto.response.cart.StockInfo;
import org.example.cy_shop.dto.response.product.stock_response.ValueResponse;
import org.example.cy_shop.entity.product.StockEntity;
import org.example.cy_shop.entity.product.ValueEntity;
import org.example.cy_shop.entity.product.ValueRequest;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.product_mapper.ValueMapper;
import org.example.cy_shop.repository.product_repository.IStockRepository;
import org.example.cy_shop.repository.product_repository.IValueRepository;
import org.example.cy_shop.service.product.IValueService;
import org.example.cy_shop.utils.Const;
import org.example.cy_shop.utils.UtilsFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ValueService implements IValueService {
    @Autowired
    IValueRepository valueRepository;
    @Autowired
    ValueMapper valueMapper;
    @Autowired
    IStockRepository stockRepository;



    @Override
    public ValueResponse findByNameAndIdOption(String value, Long idOption) {
        try {
            if(value != null)
                value = value.trim();
            ValueEntity valueEntity = valueRepository.findByNameAndIdOption(value, idOption);
            if(valueEntity == null)
                return null;
            return valueMapper.convertToResponse( valueEntity);
        }catch (Exception e){
            System.out.println("Khong the tim thay value cua option (value servcie): " + e);
            return null;
        }
    }

    @Override
    public ValueResponse findByValueAndIdOptionAndIdStock(String value, Long idOption, Long idStock) {
        try {
            if(value != null)
                value = value.trim();
            ValueEntity valueEntity = valueRepository.findByNameAndIdOptionAndIdStock(value, idOption, idStock);
            if(valueEntity == null)
                return null;

            return valueMapper.convertToResponse( valueEntity);
        }catch (Exception e){
            System.out.println("Khong the tim thay value cua option theo name, idoption va idstock (value servcie): " + e);
            return null;
        }
    }

    @Override
    public List<ValueResponse> findByOptionId(Long optionId) {
        try {
            List<ValueEntity> valueEntityList = valueRepository.findByOptionId(optionId);
            if(valueEntityList == null)
                return null;
            return valueEntityList.stream().map(it -> valueMapper.convertToResponse(it)).toList();
        }catch (Exception e){
            System.out.println("Khong the tim thay list value theo option id (value service)" + e);
            return null;
        }
    }

    @Override
    public List<ValueResponse> findDistinceByOptionId(Long id) {
        try {
            List<ValueEntity> valueEntityList = valueRepository.findDistinctByOptionId(id);
            if(valueEntityList == null)
                return null;
            return valueEntityList.stream().map(it -> valueMapper.convertToResponse(it)).toList();
        }catch (Exception e){
            System.out.println("Khong the tim distince value (value service): " + e);
            return null;
        }
    }

    @Override
    public List<Long> findIdStockByManyOptionAndValue(String option1, String value1, String option2, String value2, Long idProduct) {
        try {
            List<Long> valueEntityListSearch1 = valueRepository.getByOption(option1, value1, idProduct);
            List<Long> valueEntityListSearch2 = valueRepository.getByOption(option2, value2, idProduct);

            if(valueEntityListSearch1.size() == 0 || valueEntityListSearch2.size() == 0)
                throw new AppException(ErrorCode.PRODUCT_OPTION_NOT_FOUNT);

            List<Long> commonValues = new ArrayList<>(valueEntityListSearch1);
            commonValues.retainAll(valueEntityListSearch2);
            return commonValues;

        }catch (Exception e){
            System.out.println("Khong the tim thay value theo nhieu option va value (value service) " + e);
            return null;
        }
    }

    @Override
    public Long getQuantityByManyOptionAndValue(String option1, String value1, String option2, String value2, Long idProduct) {
        try {
            List<Long> ids = findIdStockByManyOptionAndValue(option1, value1, option2, value2, idProduct);
            return stockRepository.getQuantity(ids);
        }catch (Exception e){
            System.out.println("Loi khong the lay so luong san pham (value service): " + e);
            return 0L;
        }
    }

    @Override
    public Double getPriceByManyOptionAndValue(String option1, String value1, String option2, String value2, Long idProduct) {
        try {
            List<Long> ids = findIdStockByManyOptionAndValue(option1, value1, option2, value2, idProduct);
            return stockRepository.getPrice(ids);
        }catch (Exception e){
            System.out.println("Kgon the lay gia cua san pham (value service): " + e);
            return 0.0;
        }
    }

//    @Override
//    public String getImageByManyOptionValue(String op1, String vl1, String op2, String vl2, Long idProduct) {
//        try {
//            System.out.println("op1 = " + op1);
//            System.out.println("vl1 = " + vl1);
//            List<Long> ids = findIdStockByManyOptionAndValue(op1, vl1, op2, vl2, idProduct);
//
//            System.out.println("size = " + ids.size());
//            for (var it: ids){
//                ValueEntity valueEntity = valueRepository.findByIdStock(it);
//                if(valueEntity != null && valueEntity.getImage() != null)
//                    return valueEntity.getImage();
//            }
//            return null;
//        }catch (Exception e){
//            System.out.println("Khong the lay anh bien the (value service): " + e);
//            return null;
//        }
//    }


    @Override
    public ApiResponse<ValueResponse> save(ValueRequest valueRequest) {
        valueRequest.simpleValidate();
        ValueResponse valueFind = findByValueAndIdOptionAndIdStock(valueRequest.getOptionValue(), valueRequest.getIdOption(),
                valueRequest.getIdStock());
        if(valueFind != null){
            return new ApiResponse<>(200, "Value đã tồn tại", valueFind);
        }

        ValueEntity valueEntity = valueRepository.save(valueMapper.convertToEntity(valueRequest));

        if(valueEntity != null){
            ValueResponse valueResponse = valueMapper.convertToResponse(valueEntity);
            System.out.println("Them gia tri cua option thanh cong (value service): " + valueRequest.getOptionValue());
            return new ApiResponse<>(200, "Thêm value thành công", valueResponse);
        }else {
            System.out.println("Them gia tri cua option that bai (value service): " + valueRequest.getOptionValue());
            return new ApiResponse<>(400, "Không thể thêm value", null);

        }
    }

    //-------update ảnh cho tất cả những nơi có value request
    @Override
    public ApiResponse<String> updateValueImage(ImageOptionRequest imageOptionRequest, Long idProduct) {
        try {
            imageOptionRequest.simpleValidate();

            List<ValueEntity> valueEntity = valueRepository.existsByOptionNameAndOptionValueAndIdProduct(imageOptionRequest.getOptionName(),
                    imageOptionRequest.getOptionValue(), idProduct);

            for (var it : valueEntity) {

                it.setImage(imageOptionRequest.getImage());
                ValueEntity value = valueRepository.save(it);
            }
            return new ApiResponse<>(200, "Cập nhật ảnh cho option thành công", null);

        }catch (Exception e){
            System.out.println("Loi update hinh anh: " + e);
            return null;
        }
    }

    @Override
    public ApiResponse<StockInfo> getStockByVariantAndIdProducts(String op1, String vl1, String op2, String vl2, Long idPrd) {
        List<Long> ids = findIdStockByManyOptionAndValue(op1, vl1, op2, vl2, idPrd);
        if(ids == null || ids.size() == 0)
            throw new AppException(ErrorCode.STOCK_OF_PRODUCT_NOT_FOUND);

        StockInfo stockInfo = new StockInfo();

        //---lấy số lượng
        Long quantity = stockRepository.getQuantity(ids);
        stockInfo.setQuantity(quantity);

        //---lấy giá
        if(ids.size() == 1){
            if(op1 != null || op2 != null) {
                StockEntity stock = stockRepository.findById(ids.get(0)).orElse(null);
                if (stock == null)
                    throw new AppException(ErrorCode.STOCK_OF_PRODUCT_NOT_FOUND);

                stockInfo.setPrice(stock.getPrice());
            }
        }

        //---lấy ảnh
        boolean checkImage = false;
        for (var it: ids){
            if(checkImage == true)
                break;

            List<ValueEntity> valueEntities = valueRepository.findManyByIdStock(it);
            if(valueEntities != null && valueEntities.size() != 0) {
                for (var el: valueEntities) {
                    if(el.getImage() != null) {
                        stockInfo.setImageVariant(el.getImage());
                        checkImage = true;
                        break;
                    }
                }
            }
        }

        return new ApiResponse<>(200, "Thông tin stock", stockInfo);
    }
}
