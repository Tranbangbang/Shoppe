package org.example.cy_shop.service.impl.product;

import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.product.OptionRequest;
import org.example.cy_shop.dto.request.product.edit.ImageOptionRequest;
import org.example.cy_shop.dto.response.product.stock_response.LittleOptionResponse;
import org.example.cy_shop.dto.response.product.stock_response.OneChildrenOptionResponse;
import org.example.cy_shop.dto.response.product.stock_response.OneParentOptionResponse;
import org.example.cy_shop.dto.response.product.stock_response.OptionResponse;
import org.example.cy_shop.entity.product.OptionEntity;
import org.example.cy_shop.entity.product.ValueEntity;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.product_mapper.OptionMapper;
import org.example.cy_shop.repository.product_repository.IOptionRepository;
import org.example.cy_shop.repository.product_repository.IValueRepository;
import org.example.cy_shop.service.product.IOptionService;
import org.example.cy_shop.service.product.IValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OptionService implements IOptionService {
    @Autowired
    IOptionRepository optionRepository;
    @Autowired
    OptionMapper optionMapper;
    @Autowired
    private ProductService productService;
    @Autowired
    IValueService valueService;
    @Autowired
    IValueRepository valueRepository;

    @Override
    public String getImageOption(String optionName, String optionValue, Long idProduct) {
        List<ValueEntity> valueEntities = valueRepository.existsByOptionNameAndOptionValueAndIdProduct(optionName,
                optionValue, idProduct);
        for(var it: valueEntities){
            return it.getImage();
        }
        return null;
    }

    @Override
    public List<LittleOptionResponse> findDistinceByProductId(Long productId) {
        try {
            List<OptionEntity> optionEntityList = optionRepository.findByIdProduct(productId);
            if(optionEntityList == null)
                return null;
            return optionEntityList.stream().map(it -> optionMapper.convertToLittleOption(it)).toList();
        }catch (Exception e){
            System.out.println("Khong the tim thay little option response theo product id(option servie) " + e);
            return null;
        }
    }

    @Override
    public List<OptionResponse> findByProductId(Long id) {
        try {
            List<OptionEntity> optionEntityList = optionRepository.findByIdProduct(id);
            if(optionEntityList == null)
                return null;
            return optionEntityList.stream().map(it -> optionMapper.convertToResponse(it)).toList();
        }catch (Exception e){
            System.out.println("Khong the tim thay list option theo id(option service) " + e);
            return null;
        }
    }

    @Override
    public OptionResponse findByNameAndProductId(String name, Long productId) {
        try {
            OptionEntity optionEntity = optionRepository.findByNameAndIdProduct(name, productId);
            if(optionEntity == null)
                return null;
            return optionMapper.convertToResponse(optionEntity);
        }catch (NullPointerException eNull){
            System.out.println("Loi khong tim thay option response theo ten va id product (option service): " + eNull);
            return null;
        }
        catch (Exception e){
            System.out.println("Loi khong tim thay option response theo ten va id product (option service): " + e);
            return null;
        }

    }

    @Override
    public List<OneParentOptionResponse> findListParentResponse(Long productId) {
        try {
            List<LittleOptionResponse> littleOptionResponseList = findDistinceByProductId(productId);
            List<OneParentOptionResponse> oneParentOptionResponseList = new ArrayList<>();

            //--------------không có lựa chọn nào
            if(littleOptionResponseList == null)
                return null;

            //------------nếu chỉ co 1 lựa chọn
            if(littleOptionResponseList.size() == 1){
                for(var it: littleOptionResponseList.get(0).getOptionValue()){
                    String optionName = littleOptionResponseList.get(0).getOptionName();
                    OneParentOptionResponse oneParentOptionResponse = new OneParentOptionResponse(optionName, it, null, null, null, null);
                    oneParentOptionResponse.setImg(getImageOption(optionName, it, productId));

                    var quantity = valueService.getQuantityByManyOptionAndValue(littleOptionResponseList.get(0).getOptionName(), it,
                            null, null, productId);
                    var price = valueService.getPriceByManyOptionAndValue(littleOptionResponseList.get(0).getOptionName(), it,
                            null, null, productId);
                    oneParentOptionResponse.setQuantity(quantity);
                    oneParentOptionResponse.setPrice(price);

                    oneParentOptionResponseList.add(oneParentOptionResponse);
                }
            }

            //----------nếu có 2 lựa chọn
            else if(littleOptionResponseList.size() == 2){
               for(var par: littleOptionResponseList.get(0).getOptionValue()){
                   var oneParent = new OneParentOptionResponse(littleOptionResponseList.get(0).getOptionName(),
                           par, null, null, null, null);
                   oneParent.setImg(getImageOption(littleOptionResponseList.get(0).getOptionName(), par, productId ));

                   List<OneChildrenOptionResponse> listChild = new ArrayList<>();
                   for (var chil: littleOptionResponseList.get(1).getOptionValue()){

                       var oneChild = new OneChildrenOptionResponse(littleOptionResponseList.get(1).getOptionName(),
                               chil, null, null, null);

                       //--------Lấy giá và số lượng
                       var quantity = valueService.getQuantityByManyOptionAndValue(oneParent.getOptionName(), oneParent.getOptionValue(),
                               oneChild.getOptionName(), oneChild.getOptionValue(), productId);
                       var price = valueService.getPriceByManyOptionAndValue(oneParent.getOptionName(), oneParent.getOptionValue(),
                               oneChild.getOptionName(), oneChild.getOptionValue(), productId);

                       //---------------set lại
                       oneChild.setQuantity(quantity);
                       oneChild.setPrice(price);

                       listChild.add(oneChild);
                   }
                   oneParent.setOptions(listChild);
                   oneParentOptionResponseList.add(oneParent);
               }
            }

            return oneParentOptionResponseList;

        }catch (Exception e){
            System.out.println("Loi khi lay list parent option (option service): " + e);
            return null;
        }
    }

    @Override
    public ApiResponse<OptionResponse> save(OptionRequest optionRequest) {
        String name = optionRequest.getOptionName();
        Long productId = optionRequest.getIdProduct();
        if(name != null)
            name = name.trim();

        Boolean exits = productService.checkExits(productId);
        if(exits ==null)
            throw new AppException(ErrorCode.PRODUCT_OF_OPTION_NOT_FOUND);

        //-----------------Nếu option đã tồn tại => trả ra đối tượng option đó---------------
        OptionResponse optionFind = findByNameAndProductId(name, productId);
        if(optionFind != null){
            System.out.println("Option da ton tai (option service): " + optionRequest.getOptionName());
            return new ApiResponse<>(200, "Option đã tồn tại", optionFind);
        }


        //---------------------Nếu option chưa tồn tại => lưu option và trả ra option vừa được lưu
        OptionEntity optionEntitySave = new OptionEntity(null, name, productId,  optionRequest.getVersionUpdate(), null);
        OptionEntity optionEntity = optionRepository.save(optionEntitySave);

        if(optionEntity != null){
            OptionResponse optionResponse = optionMapper.convertToResponse(optionEntity);
            System.out.println("Them option thanh cong (option service): " + optionRequest.getOptionName());
            return new ApiResponse<>(200, "Thêm option thành công", optionResponse);
        }else {
            System.out.println("Them option that bai (option service): " + optionRequest.getOptionName());
            return new ApiResponse<>(400, "Không thể thêm option", null);
        }
    }


}
