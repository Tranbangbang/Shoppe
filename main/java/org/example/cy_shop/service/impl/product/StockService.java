package org.example.cy_shop.service.impl.product;

import jakarta.transaction.Transactional;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.order.VariantDTO;
import org.example.cy_shop.dto.request.product.add.ManyStockRequest;
import org.example.cy_shop.dto.request.product.OptionRequest;
import org.example.cy_shop.dto.request.product.add.StockRequest;
import org.example.cy_shop.dto.request.product.edit.ImageOptionRequest;
import org.example.cy_shop.dto.response.cart.StockInfo;
import org.example.cy_shop.dto.response.product.stock_response.OptionResponse;
import org.example.cy_shop.dto.response.product.stock_response.StockResponse;
import org.example.cy_shop.dto.response.product.stock_response.ValueResponse;
import org.example.cy_shop.entity.product.*;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.product_mapper.StockMapper;
import org.example.cy_shop.repository.product_repository.IOptionRepository;
import org.example.cy_shop.repository.product_repository.IProductRepository;
import org.example.cy_shop.repository.product_repository.IStockRepository;
import org.example.cy_shop.repository.product_repository.IValueRepository;
import org.example.cy_shop.service.product.IOptionService;
import org.example.cy_shop.service.product.IStockService;
import org.example.cy_shop.service.product.IValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class StockService implements IStockService {
    @Autowired
    StockMapper stockMapper;
    @Autowired
    IOptionService optionService;
    @Autowired
    IValueService valueService;
    @Autowired
    private ProductService productService;
    @Autowired
    IProductRepository productRepository;
    @Autowired
    IOptionRepository optionRepository;
    @Autowired
    IValueRepository valueRepository;
    @Autowired
    IStockRepository stockRepository;


    @Override
    public StockResponse findById(Long id) {
        try {
            StockEntity stockEntity = stockRepository.findById(id).orElse(null);
            if(stockEntity == null)
                return null;
            return  stockMapper.convertToResponse(stockEntity);
        }catch (Exception e){
            System.out.println("Loi khong the tim thay stock theo id(stock servie): " + e);
            return null;
        }
    }

    @Override
    public List<StockResponse> findByProductId(Long productId) {
        try {
            List<StockEntity> stockEntity = stockRepository.findByProductId(productId);
            if(stockEntity.size() == 0)
                return null;
            return stockEntity.stream().map(it -> stockMapper.convertToResponse(it)).toList();
        }catch (Exception e){
            System.out.println("Loi khi tim stock theo productId(stock service): " + e);
            return null;
        }
    }

    @Override
    public ApiResponse<StockResponse> save(StockRequest stockRequest, Long idProduct, Long vesionUpdate) {

        //Bước 1: tạo stock và lấy được id của stock---------------
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(idProduct);

        StockEntity stockEntitySave = stockMapper.convertToStockEntity(stockRequest);
        stockEntitySave.setVersionUpdate(vesionUpdate);
        stockEntitySave.setProduct(productEntity);

        StockEntity stockEntity = stockRepository.save(stockEntitySave);
        if(stockEntity == null){
            return new ApiResponse<>(400, "Không thể thêm stock", null);
        }

        //Bước 2: Luu option va value ------------------------------
        for (var it: stockRequest.getOptionValueRequest()){
            //2.1. --------------save option---------------------
            OptionRequest optionRequestSave = new OptionRequest(null, it.getOptionName(), idProduct, vesionUpdate);
            ApiResponse<OptionResponse> optionSave = optionService.save(optionRequestSave);
            if(optionSave.getCode() != 200) {
                System.out.println("Khong the luu option (stock service)");
                throw new AppException(ErrorCode.CAN_NOT_SAVE_STOCK);
            }

            //---------------save value-------------------------
            Long idOption = optionSave.getData().getId();

            ValueRequest valueRequestSave = new ValueRequest(null, it.getOptionValue(), idOption, stockEntity.getId(), idProduct, vesionUpdate);
            ApiResponse<ValueResponse> valueSave = valueService.save(valueRequestSave);
            if(valueSave.getCode() != 200) {
                System.out.println("Khong the luu value (stock service)");
                throw new AppException(ErrorCode.CAN_NOT_SAVE_STOCK);
            }
        }
        if(stockEntity != null) {
            return new ApiResponse<>(200, "Thêm stock thành công", stockMapper.convertToResponse(stockEntity));
        }
        else
            return new ApiResponse<>(400, "Không thể thêm stock", null);
    }


    @Override
    public ApiResponse<String> saveMany(ManyStockRequest manyStockRequest, Boolean update) {
        //-----------------nếu sản phẩm đã được tạo dữ liệu ==============> không cho tạo thêm nữa
        String mess = null;
        try {
            Long vesionUpdate = 1L;
            ProductEntity productEntity = productRepository.findById(manyStockRequest.getIdProduct()).orElse(null);
            if(productEntity == null){
                System.out.println("id product = " + manyStockRequest.getIdProduct());
                throw new AppException(ErrorCode.PRODUCT_OF_STOCK_REQUEST_NOT_FOUND);
            }

            if(manyStockRequest.getStockRequestList() == null || manyStockRequest.getStockRequestList().size() == 0){
                mess = "Thiếu thông tin stock";
                try {
                    productRepository.delete(productEntity);
                }catch (Exception e){
                    System.out.println("Khong xoa duoc san pham");
                }
                throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }

            if(productEntity.getVersionUpdate() != null) {
                if(update == false)
                    throw new AppException(ErrorCode.STOCK_OF_PRODUCT_WAS_CREATED);
                if(update == true)
                    vesionUpdate ++;
            }

            //-----Bước 1: Cập nhật giá trị của stock
            //-------------------------------thực hiện lưu option, value và stock
            //-------------------------------cập nhật version update của product
            //...trường hợp 1: list stockRequest rỗng (lấy giá trị base)
            //...Nếu baase cũng trống => exception

            if(manyStockRequest.getStockRequestList() == null || manyStockRequest.getStockRequestList().size() == 0){

                if(manyStockRequest.getBasePrice() == 0.0 && manyStockRequest.getBaseQuantity() == 0) {
                    throw new AppException(ErrorCode.STOCK_MISSING_INFO);
                }

                ProductEntity prd = new ProductEntity();
                prd.setId(manyStockRequest.getIdProduct());
                StockEntity stockEntity = new StockEntity(null, manyStockRequest.getBaseQuantity(), manyStockRequest.getBasePrice(),
                        vesionUpdate, null, prd);
                StockEntity stock = stockRepository.save(stockEntity);
                if(stock == null)
                    return new ApiResponse<>(400, "Không thể lưu stock", null);
            }

            else {
                for (var it : manyStockRequest.getStockRequestList()) {
                    if(it.getQuantity() == null || it.getPrice() == null){
                        mess = "Thiếu thông tin quantity hoặc price";
//                        productEntity.setIsDelete(true);
//                        productRepository.save(productEntity);
                        try {
                            productRepository.delete(productEntity);
                        }catch (Exception e){
                            System.out.println("Khong xoa duoc san pham");
                        }

                        throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
                    }
                    if(it.getQuantity() > 1000000 || it.getPrice() > 1000000000){
                        mess = "Thông tin quan tity hoặc price không hợp lệ";
//                        productEntity.setIsDelete(true);
//                        productRepository.save(productEntity);
                        try {
                            productRepository.delete(productEntity);
                        }catch (Exception e){
                            System.out.println("Khong xoa duoc san pham");
                        }
                        throw new AppException(ErrorCode.UNCATEGORIZED_EXCEPTION);
                    }
                    save(it, manyStockRequest.getIdProduct(), vesionUpdate);
                }
            }

            //----------Bước 2: Cập nhật ảnh info của option-------

            System.out.println("Chuẩn bị lưu ảnh ");
            System.out.println(manyStockRequest.getImageInfo());
            for (var it: manyStockRequest.getImageInfo()){
                System.out.println("In    this: " + it.getOptionName() + ", " + it.getOptionValue());
                valueService.updateValueImage(it, manyStockRequest.getIdProduct());
            }

//            for (var it: manyStockRequest.getStockRequestList()){
//                for (var itImg: it){
//                    valueService.updateValueImage(itImg, manyStockRequest.getIdProduct());
//                }
//            }
//
//            for(int i = 0; i < manyStockRequest.getStockRequestList().size(); i++){
//                for (int j = 0; j < )
//            }

            //--Bước 3-------cập nhật version của product
            productEntity.setVersionUpdate(vesionUpdate);
            productRepository.save(productEntity);


            System.out.println("Them thanh cong ");
            return new ApiResponse<>(200, "Thêm stock thành công", null);
        }catch (Exception e){
            System.out.println("Err: " + e );
            return new ApiResponse<>(400, mess, null);
        }
    }

    @Override
    public ApiResponse<String> deleteMany(Long idProduct) {
        try {
            List<ValueEntity> valueEntityList = valueRepository.findByIdProduct(idProduct);
            for(var it: valueEntityList){
                valueRepository.deleteById(it.getId());
            }

            List<OptionEntity> optionEntityList = optionRepository.findByIdProduct(idProduct);
            for (var it: optionEntityList){
                optionRepository.deleteById(it.getId());
            }

            List<StockEntity> stockEntityList = stockRepository.findByIdProduct(idProduct);
            for(var it: stockEntityList){
                stockRepository.deleteById(it.getId());
            }
            return new ApiResponse<>(200, "Xóa thành công", null);
        }catch (Exception e){
            return new ApiResponse<>(400, "Xóa thất bại", null);
        }
    }

    @Override
    public ApiResponse<String> update(ManyStockRequest manyStockRequest) {
        deleteMany(manyStockRequest.getIdProduct());
        saveMany(manyStockRequest, true);
        return new ApiResponse<>(200, "Cập nhận stock thành công", null);
    }

    @Override
    public List<Long> findIdsStockByVariants(List<VariantDTO> variantDTOS, Long idProduct) {
        try {
            String op1 = null, vl1 = null, op2 = null, vl2 = null;
            if(variantDTOS != null){
                if(variantDTOS.size() == 1){
                    op1 = variantDTOS.get(0).getOptionName();
                    vl1 = variantDTOS.get(0).getOptionValue();
                }
                if(variantDTOS.size() == 2){
                    op1 = variantDTOS.get(0).getOptionName();
                    vl1 = variantDTOS.get(0).getOptionValue();
                    op2 = variantDTOS.get(1).getOptionName();
                    vl2 = variantDTOS.get(1).getOptionValue();
                }
            }
            List<Long> ids = valueService.findIdStockByManyOptionAndValue(op1, vl1, op2, vl2, idProduct);
            return ids;
        }catch (Exception e){
            System.out.println("Loi lay ids stock theo variants: " + e);
            return null;
        }
    }

    @Override
    public StockInfo getStockInfo(List<VariantDTO> variantDTOS, Long idProduct) {
        StockInfo stockInfo = new StockInfo();
        //2 trường hợp (nếu variants null: có thể có giá và số lượng mặc định)
        //th2: nếu chỉ tìm thấy 1 bản ghi duy nhất => true

        //th1: giá và số lượng mặc định
        if(variantDTOS == null || variantDTOS.size() == 0){
            List<StockEntity> stockEntity = stockRepository.findByIdProduct(idProduct);
            if(stockEntity != null && stockEntity.size() == 1){
                stockInfo.setQuantity(stockEntity.get(0).getQuantity());
                stockInfo.setPrice(stockEntity.get(0).getPrice());
            }
        }

        //th2: tìm theo optionName và optionValue
        List<Long> ids = findIdsStockByVariants(variantDTOS, idProduct);
        if(ids == null) {
//            System.out.println("ids null");
            return null;
        }
        if(ids.size() != 1)
            return null;

        //--------Lấy ra giá và số lượng trong bảng stock
        StockEntity stock2 = stockRepository.findById(ids.get(0)).orElse(null);
        if(stock2 == null)
            return null;


        //-------Lấy ra ảnh biến thể trong bảng value
//        String imageVariant = null;
//        for(var it: ids){
//            ValueEntity valueEntity = valueRepository.findByIdStock(it);
//            if(valueEntity != null && valueEntity.getImage() != null){
//                imageVariant = valueEntity.getImage();
//                break;
//            }
//        }

        String imageVariant = null;
        for(var it: variantDTOS){
            List<ValueEntity> valueEntity = valueRepository.existsByOptionNameAndOptionValueAndIdProduct(
                    it.getOptionName(), it.getOptionValue(), idProduct);
            for (var el: valueEntity){
                if(el != null && el.getImage() != null){
                    imageVariant = el.getImage();
                    break;
                }
            }
        }


        stockInfo.setQuantity(stock2.getQuantity());
        stockInfo.setPrice(stock2.getPrice());
        stockInfo.setImageVariant(imageVariant);
        stockInfo.setId(stock2.getId());

        return stockInfo;
    }

    @Override
    public Long getAllQuantityByIdProduct(Long idPrd) {
        try {
            Long quantity = stockRepository.getQuantityByIdPrd(idPrd);
            return quantity;
        }catch (Exception e){
            System.out.println("Loi lay so luong theo san pham: " + e);
            return null;
        }
    }

    @Override
    public Double getMinPriceByIdPrd(Long idPrd) {
        try {
            return stockRepository.getMinPriceByIdPrd(idPrd);
        }catch (Exception e){
            System.out.println("Khong the lay gia nho nhat theo id prd");
            return null;
        }
    }

    @Override
    public Double getMaxPriceByIdPrd(Long idPrd) {
        try {
            return stockRepository.getMaxPriceByIdPrd(idPrd);
        }catch (Exception e){
            System.out.println("Khong the lay gia nho nhat theo id prd");
            return null;
        }
    }
}
