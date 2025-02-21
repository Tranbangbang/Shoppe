package org.example.cy_shop.service.impl.shop_statics;

import jakarta.transaction.Transactional;
import org.example.cy_shop.controller.Shop.ShopController;
import org.example.cy_shop.controller.client.product.ClientProductController;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.response.product.ProductResponse;
import org.example.cy_shop.dto.response.shop.shop_static.ShopInforResponse;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.Shop;
import org.example.cy_shop.entity.product.ProductEntity;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.product_mapper.ProductMapper;
import org.example.cy_shop.mapper.shop_static_mapper.ShopStaticMapper;
import org.example.cy_shop.repository.IShopRepository;
import org.example.cy_shop.repository.product_repository.IProductRepository;
import org.example.cy_shop.service.IAccountService;
import org.example.cy_shop.service.impl.order.OrderSchedule;
import org.example.cy_shop.service.impl.order.OrderService;
import org.example.cy_shop.service.impl.product.ProductService;
import org.example.cy_shop.service.product.IProductService;
import org.example.cy_shop.service.shop_statics.IShopStaticService;
import org.example.cy_shop.specification.ProductSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ShopStaticsService implements IShopStaticService {
    @Autowired
    IShopRepository shopRepository;
    @Autowired
    ShopStaticMapper shopStaticMapper;
    @Autowired
    IAccountService accountService;
    @Autowired
    ProductSpecification productSpecification;
    @Autowired
    IProductRepository productRepository;
    @Autowired
    ProductMapper productMapper;

    @Override
    public ApiResponse<ShopInforResponse> getShopInfo(Long idShop) {
        Shop shop = shopRepository.findById(idShop).orElse(null);

        if(shop == null)
            throw new AppException(ErrorCode.SHOP_CAN_NOT_FOUND);
        if(shop.getIsApproved() == false)
            throw new AppException(ErrorCode.SHOP_NOT_ACTIVE);

        return new ApiResponse<>(200, "Thông tin shop", shopStaticMapper.convertToShopInforResponse(shop));
    }

    @Override
    public ApiResponse<ShopInforResponse> getShopInfo() {
        Account account = accountService.getMyAccount();
        if(account == null)
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);

//        System.out.println("email: " + account.getEmail());
        Shop shop = shopRepository.findByEmail(account.getEmail());
        if(shop == null)
            throw new AppException(ErrorCode.SHOP_NOT_FOUND);
        if(shop.getIsApproved() == false)
            throw new AppException(ErrorCode.SHOP_NOT_ACTIVE);
        return new ApiResponse<>(200, "Thông tin shop", shopStaticMapper.convertToShopInforResponse(shop));
    }

    @Override
    public ApiResponse<Page<ProductResponse>> getMyProductShop(Long idShop, Long idCategory, Boolean newPrd, Boolean popular, Pageable pageable) {
        try {
            Shop shop = null;
            if(idShop == null) {
                Account account = accountService.getMyAccount();
                if (account == null)
                    throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
                shop = shopRepository.findByEmail(account.getEmail());
            } else if (idShop != null) {
                shop = shopRepository.findById(idShop).orElse(null);
            }

            if (shop == null)
                throw new AppException(ErrorCode.SHOP_NOT_FOUND);
            if (shop.getIsApproved() == false)
                throw new AppException(ErrorCode.SHOP_NOT_ACTIVE);

            Specification<ProductEntity> productSpe = Specification
                    .where(productSpecification.hasCategoryId(idCategory))
                    .and(productSpecification.hasIdShop(shop.getId()))
                    .and(productSpecification.hasNewProduct(newPrd))
                    .and(productSpecification.sortByPopular(popular));

            Page<ProductEntity> product = productRepository.findAll(productSpe, pageable);
            var result = product.map(it -> productMapper.convertToResponse(it));
            return new ApiResponse<>(200, "Danh sách sản phẩm", result);
        } catch (Exception e) {
            System.out.println("Khong the lay san pham cua shop(shop static service): " + e);
            String mess = "Lối: " + e.getMessage();
            return new ApiResponse<>(400, mess, null);
        }
    }

}
//ShopC
//ProductService
//OrderService
//OrderSchedule
//ClientProductController