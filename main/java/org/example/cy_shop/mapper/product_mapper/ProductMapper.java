package org.example.cy_shop.mapper.product_mapper;

import org.example.cy_shop.dto.request.product.add.ProductAddRequest;
import org.example.cy_shop.dto.response.feedback.CountFeedbackResponse;
import org.example.cy_shop.dto.response.product.MediaProductResponse;
import org.example.cy_shop.dto.response.product.ProductDetailForEditResponse;
import org.example.cy_shop.dto.response.product.ProductResponse;
import org.example.cy_shop.dto.response.product.stock_response.OneParentOptionResponse;
import org.example.cy_shop.dto.response.product.stock_response.ProductSearchResponse;
import org.example.cy_shop.entity.Shop;
import org.example.cy_shop.entity.product.CategoryEntity;
import org.example.cy_shop.entity.product.ProductEntity;
import org.example.cy_shop.enums.order.StatusOrderEnum;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.repository.IShopRepository;
import org.example.cy_shop.repository.product_repository.ICategoryRepository;
import org.example.cy_shop.repository.product_repository.IMediaProductRepository;
import org.example.cy_shop.repository.product_repository.IProductRepository;
import org.example.cy_shop.service.feedback.IFeedbackService;
import org.example.cy_shop.service.impl.product.ProductService;
import org.example.cy_shop.service.impl.product.StockService;
import org.example.cy_shop.service.product.IMediaProductService;
import org.example.cy_shop.service.product.IOptionService;
import org.example.cy_shop.enums.TypeMediaEnum;
import org.example.cy_shop.service.product.IStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductMapper {
    @Autowired
    IMediaProductRepository imageProductRepository;
    @Autowired
    ICategoryRepository categoryRepository;
    @Autowired
    IShopRepository shopRepository;
    @Autowired
    IProductRepository productRepository;
    @Autowired
    IMediaProductService mediaProductService;
    @Autowired
    IOptionService optionService;
    @Autowired
    private ProductService productService;
    @Autowired
    private IStockService iStockService;
    @Autowired
    private StockService stockService;
    @Autowired
    IFeedbackService feedbackService;


    public ProductResponse convertToResponse(ProductEntity productEntity) {
        Long quantity = stockService.getAllQuantityByIdProduct(productEntity.getId());

        String coverImageSrc = null;
        String introViseoSrc = null;

        List<MediaProductResponse> mediaProductResponseList = mediaProductService.findByProductId(productEntity.getId());
        List<String> coverImage = mediaProductService.getSrcOfTypeMedia(mediaProductResponseList, TypeMediaEnum.COVER_IMAGE.name());
        List<String> detailImage = mediaProductService.getSrcOfTypeMedia(mediaProductResponseList, TypeMediaEnum.DETAIL_IMAGE.name());
        List<String> introVideo = mediaProductService.getSrcOfTypeMedia(mediaProductResponseList, TypeMediaEnum.INTRO_VIDEO.name());

        if (coverImage != null && coverImage.size() != 0)
            coverImageSrc = coverImage.get(0);
        if (introVideo != null && introVideo.size() != 0)
            introViseoSrc = introVideo.get(0);


        List<String> imageProduct = new ArrayList<>();

        Double ratingAverage = null;
        try {
            ratingAverage = feedbackService.getRatingAve(productEntity.getId());
        } catch (Exception e) {
            System.out.println("Khong lay duoc rating (product mapper): " + e);
        }

        CountFeedbackResponse countFb = null;
        try {
            countFb = feedbackService.findCountByIdProduct(productEntity.getId());
        } catch (Exception e) {
            System.out.println("Khong the lay feedback count(product mapper): " + e);
        }
        Long cntSeller = 0L;
        try {
            if (productEntity != null && productEntity.getOrderDetail() != null) {
                for (var it : productEntity.getOrderDetail()) {
                    if (it.getOrder().getStatusOrder().name().equalsIgnoreCase(StatusOrderEnum.RECEIVED.name())) {
                        cntSeller += it.getQuantity();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Khong the lay so luong ban(product mapper) " + e);
        }

        return ProductResponse.builder()
                .id(productEntity.getId())
                .productName(productEntity.getProductName())
                .productCode(productEntity.getProductCode())
                .productDescription(productEntity.getProductDescription())

                .coverImage(coverImageSrc)
                .detailImage(detailImage)
                .introVideo(introViseoSrc)

                .categoryId(productEntity.getCategory().getId())
                .categoryName(productEntity.getCategory().getName())

                .shopId(productEntity.getShop().getId())
                .shopName(productEntity.getShop().getShopName())

                .isActive(productEntity.getIsActive())
                .isBan(productEntity.getIsBan())
                .isDelete(productEntity.getIsDelete())

                .allQuantity(quantity)
                .minPrice(stockService.getMinPriceByIdPrd(productEntity.getId()))
                .maxPrice(stockService.getMaxPriceByIdPrd(productEntity.getId()))

                .ratingAverage(ratingAverage)
                .countFb(countFb)
                .countSeller(cntSeller)

                .createDate(productEntity.getCreateDate())
                .modifierDate(productEntity.getModifierDate())
                .createBy(productEntity.getCreateBy())
                .modifierBy(productEntity.getModifierBy())
                .build();
    }

    public ProductEntity convertToEntity(ProductAddRequest request) {
        //entity phụ thuộc
        Long categoryId = null;
        CategoryEntity categoryEntity = new CategoryEntity();

        Long idShop = null;
        Shop shop = new Shop();

        String productCode = null;


        if (request != null) {
            //----------------------category--------------------
            categoryId = request.getCategoryId();
            if (categoryId == null) {
                System.out.println("Err: category id cua product null (product mapper)");
                throw new AppException(ErrorCode.CATEGORY_OF_PRODUCT_EMPTY);
            }

            categoryEntity = categoryRepository.findById(categoryId).orElse(null);
            if (categoryEntity == null) {
                System.out.println("Err: category tham chieu cua product khong ton tai (product mapper)");
                throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
            }

            if (categoryEntity.getLevel() == 1)
                throw new AppException(ErrorCode.CATEGORY_NOT_ALLOW);

            //----------------------shop--------------------
            idShop = request.getShopId();
            if (idShop == null)
                throw new AppException(ErrorCode.SHOP_OF_PRODUCT_EMPTY);
            boolean hasShop = shopRepository.existsById(idShop);
            if (hasShop == false) {
                throw new AppException(ErrorCode.SHOP_CAN_NOT_FOUND);
            } else {
                shop.setId(idShop);
            }
//            shop = shopRepository.findById(idShop).orElse(null);
//            if(shop == null)
//                throw new AppException(ErrorCode.SHOP_CAN_NOT_FOUND);

            //----------------------Product code--------------------
            productCode = request.generateProductCode(shop.getShopName(), request.getProductName());

            while (true) {
                ProductEntity productEntity = productRepository.findByProductCode(productCode);
                if (productEntity != null) {
                    System.out.println("Err: Ma san pham nay da bi trung (product mapper)");
                    productCode = request.generateProductCode(shop.getShopName(), request.getProductName());
                } else {
                    System.out.println("Success: Tao ma san pham thanh cong (product mapper)");
                    break;
                }
            }
        }


        //------------------------------------------------------------

        return ProductEntity.builder()
                .productCode(productCode)
                .productName(request.getProductName())
                .productDescription(request.getProductDescription())
                .category(categoryEntity)
                .shop(shop)
                .isActive(true)
                .build();
    }

    public ProductDetailForEditResponse convertToProductDetailForEditResponse(ProductEntity productEntity) {
        ProductDetailForEditResponse productDetailForEditResponse = new ProductDetailForEditResponse();
//            List<OptionResponse> optionResponseList = optionService.findByProductId(idProduct);
        List<OneParentOptionResponse> optionResponseList = optionService.findListParentResponse(productEntity.getId());
        productDetailForEditResponse.setOption(optionResponseList);

        ProductResponse productResponse = productService.findById(productEntity.getId());
        productDetailForEditResponse.setProduct(productResponse);


        return productDetailForEditResponse;
    }


    public ProductSearchResponse toResponse(ProductEntity productEntity) {
        Long quantity = stockService.getAllQuantityByIdProduct(productEntity.getId());

        String coverImageSrc = null;
        String introViseoSrc = null;

        List<MediaProductResponse> mediaProductResponseList = mediaProductService.findByProductId(productEntity.getId());
        List<String> coverImage = mediaProductService.getSrcOfTypeMedia(mediaProductResponseList, TypeMediaEnum.COVER_IMAGE.name());
        List<String> detailImage = mediaProductService.getSrcOfTypeMedia(mediaProductResponseList, TypeMediaEnum.DETAIL_IMAGE.name());
        List<String> introVideo = mediaProductService.getSrcOfTypeMedia(mediaProductResponseList, TypeMediaEnum.INTRO_VIDEO.name());

        if (coverImage != null && coverImage.size() != 0)
            coverImageSrc = coverImage.get(0);
        if (introVideo != null && introVideo.size() != 0)
            introViseoSrc = introVideo.get(0);


        List<String> imageProduct = new ArrayList<>();

        return ProductSearchResponse.builder()
                .id(productEntity.getId())
                .productName(productEntity.getProductName())
                .productCode(productEntity.getProductCode())
                .productDescription(productEntity.getProductDescription())

                .coverImage(coverImageSrc)
                .detailImage(detailImage)
                .introVideo(introViseoSrc)

                .categoryId(productEntity.getCategory().getId())
                .categoryName(productEntity.getCategory().getName())

                .shopId(productEntity.getShop().getId())
                .shopName(productEntity.getShop().getShopName())

                .isActive(productEntity.getIsActive())
                .isBan(productEntity.getIsBan())
                .isDelete(productEntity.getIsDelete())

                .allQuantity(quantity)
                .minPrice(stockService.getMinPriceByIdPrd(productEntity.getId()))
                .maxPrice(stockService.getMaxPriceByIdPrd(productEntity.getId()))
                .build();
    }
}
