package org.example.cy_shop.mapper.order;

import org.example.cy_shop.configuration.jwtConfig.JwtProvider;
import org.example.cy_shop.dto.request.cart.CartRequest;
import org.example.cy_shop.dto.request.cart.OptionCartRequest;
import org.example.cy_shop.dto.request.order.VariantDTO;
import org.example.cy_shop.dto.response.cart.CartResponse;
import org.example.cy_shop.dto.response.cart.OptionCartResponse;
import org.example.cy_shop.dto.response.cart.StockInfo;
import org.example.cy_shop.dto.response.product.ProductResponse;
import org.example.cy_shop.entity.order.CartEntity;
import org.example.cy_shop.entity.product.ProductEntity;
import org.example.cy_shop.entity.product.ValueEntity;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.repository.IAccountRepository;
import org.example.cy_shop.repository.product_repository.IOptionRepository;
import org.example.cy_shop.repository.product_repository.IProductRepository;
import org.example.cy_shop.repository.product_repository.IValueRepository;
import org.example.cy_shop.service.impl.product.StockService;
import org.example.cy_shop.service.order.ICartService;
import org.example.cy_shop.service.product.IProductService;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CartMapper {
    @Autowired
    IAccountRepository accountRepository;
    @Autowired
    IOptionRepository optionRepository;
    @Autowired
    IProductRepository productRepository;
    @Autowired
    IValueRepository valueRepository;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    IProductService productService;
    @Autowired
    ICartService cartService;
    @Autowired
    VariantMapper variantMapper;
    @Autowired
    private StockService stockService;


    public CartResponse convertToResponse(CartEntity cartEntity){


        ProductResponse productResponse = productService.findById(cartEntity.getProductId());
        if(productResponse == null)
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUNT);

        List<VariantDTO> variantDTO = variantMapper.convertStringToDTO(cartEntity.getVariant());
        StockInfo stockInfo = stockService.getStockInfo(variantDTO, cartEntity.getProductId());

        Long quantity = null;
        Double price = null;
        String imgVar = null;
        if(stockInfo != null){
            quantity = stockInfo.getQuantity();
            price = stockInfo.getPrice();
            imgVar = stockInfo.getImageVariant();
        }

        return CartResponse.builder()
                .id(cartEntity.getId())
                .productId(cartEntity.getProductId())
                .productName(productResponse.getProductName())
                .quantityCart(cartEntity.getQuantity())
                .allQuantity(quantity)
                .priceEach(price)
                .option(variantDTO)
//                .imageVariant(stockInfo.getImageVariant())
                .imageVariant(imgVar)
                .status(cartEntity.getStatus())

                .idShop(productResponse.getShopId())
                .shopName(productResponse.getShopName())
                .build();
    }

    public CartEntity convertToEntity(CartRequest cartRequest){
        String variant = "";

        if(cartRequest == null)
            throw new AppException(ErrorCode.CART_REQUEST_INVALID);

        //-------kiểm tra product có tồn tại không

        ProductEntity prd = productRepository.findByIdAndActive(cartRequest.getIdProduct());

        if(prd == null) {
            throw new AppException(ErrorCode.PRODUCT_OF_CART_NOT_FOUND);
        }

        //--------kiểm tra option hoặc value có tồn tại không
        if(cartRequest.getOption() != null){
            for(var it: cartRequest.getOption()){
                if(optionRepository.existsByOptionNameAndIdProduct(it.getOptionName(), cartRequest.getIdProduct()) == false){
                    throw new AppException(ErrorCode.PRODUCT_NOT_HAS_OPTION);
                }

                List<ValueEntity> valueEntityList = valueRepository.existsByOptionNameAndOptionValueAndIdProduct(it.getOptionName(), it.getOptionValue(), cartRequest.getIdProduct());
                if( valueEntityList == null || valueEntityList.size() == 0){
                    throw new AppException(ErrorCode.PRODUCT_NOT_HAS_OPTION);
                }

                variant = variant + it.getOptionName() + Const.SEPARATOR + it.getOptionValue() + "\n";
            }
        }

        return CartEntity.builder()
                .id(null)
                .quantity(cartRequest.getQuantity())
                .variant(variant)
                .productId(cartRequest.getIdProduct())
                .build();
    }

    OptionCartResponse convertToOptionCartResponse(OptionCartRequest cartRequest){
        return OptionCartResponse.builder()
                .optionName(cartRequest.getOptionName())
                .optionValue(cartRequest.getOptionValue())
                .build();
    }

    Long getAllQuantity(List<OptionCartRequest> options, Long idProduct){
        String op1 = null, vl1 = null, op2 = null, vl2 = null;
        if(options.size() == 1) {
            op1 = options.get(0).getOptionName();
            vl1 = options.get(0).getOptionValue();
        }
        if(options.size() == 2){
            op1 = options.get(0).getOptionName();
            vl1 = options.get(0).getOptionValue();
            op2 = options.get(1).getOptionName();
            vl2 = options.get(1).getOptionValue();
        }

        System.out.println("op1 = " + op1 + ", " + vl1);
        System.out.println("op2 = " + op2 + ", " + vl2);
        System.out.println("so luong: " + cartService.getQuantityByManyOption(op1, vl1, op2, vl2, idProduct));

        return cartService.getQuantityByManyOption(op1, vl1, op2, vl2, idProduct);
    }
}
