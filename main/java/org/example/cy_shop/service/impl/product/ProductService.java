package org.example.cy_shop.service.impl.product;

import jakarta.transaction.Transactional;
import org.example.cy_shop.configuration.jwtConfig.JwtProvider;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.order.VariantDTO;
import org.example.cy_shop.dto.request.product.add.ManyStockRequest;
import org.example.cy_shop.dto.request.product.add.MediaProductRequest;
import org.example.cy_shop.dto.request.product.delete.ProductKeyRequest;
import org.example.cy_shop.dto.request.product.add.ProductAddRequest;
import org.example.cy_shop.dto.request.product.delete.ProductKeysRequest;
import org.example.cy_shop.dto.request.product.edit.ProductEditRequest;
import org.example.cy_shop.dto.request.search.SearchProduct;
import org.example.cy_shop.dto.response.cart.CartResponse;
import org.example.cy_shop.dto.response.cart.StockInfo;
import org.example.cy_shop.dto.response.product.LittleProductResponse;
import org.example.cy_shop.dto.response.product.ProductRecommendationResponse;
import org.example.cy_shop.dto.response.product.stock_response.OneParentOptionResponse;
import org.example.cy_shop.dto.response.product.ProductDetailForEditResponse;
import org.example.cy_shop.dto.response.product.ProductResponse;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.SearchHistory;
import org.example.cy_shop.entity.Shop;
import org.example.cy_shop.entity.order.CartEntity;
import org.example.cy_shop.entity.order.OrderEntity;
import org.example.cy_shop.entity.order.TrackingEntity;
import org.example.cy_shop.entity.product.MediaProductEntity;
import org.example.cy_shop.entity.product.ProductEntity;
import org.example.cy_shop.enums.StatusCartEnum;
import org.example.cy_shop.enums.order.StatusOrderEnum;
import org.example.cy_shop.enums.user.TypeUserEnum;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.order.VariantMapper;
import org.example.cy_shop.mapper.product_mapper.LittleProductMapper;
import org.example.cy_shop.mapper.product_mapper.ProductMapper;
import org.example.cy_shop.repository.IAccountRepository;
import org.example.cy_shop.repository.ISearchHistoryRepository;
import org.example.cy_shop.repository.IShopRepository;
import org.example.cy_shop.repository.order.ICartRepository;
import org.example.cy_shop.repository.order.IOrderRepository;
import org.example.cy_shop.repository.order.ITrackingRepository;
import org.example.cy_shop.repository.product_repository.IMediaProductRepository;
import org.example.cy_shop.repository.product_repository.IProductRepository;
import org.example.cy_shop.service.IAccountService;
import org.example.cy_shop.service.IShopService;
import org.example.cy_shop.service.impl.ReportServiceImpl;
import org.example.cy_shop.service.impl.order.OrderSchedule;
import org.example.cy_shop.service.impl.order.OrderService;
import org.example.cy_shop.service.order.ICartService;
import org.example.cy_shop.service.order.ITrackingService;
import org.example.cy_shop.service.product.*;
import org.example.cy_shop.specification.ProductSpecification;
import org.example.cy_shop.utils.Const;
import org.example.cy_shop.enums.TypeMediaEnum;
import org.example.cy_shop.utils.UtilsFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService implements IProductService {
    @Autowired
    ProductMapper productMapper;
    @Autowired
    IProductRepository productRepository;
    @Autowired
    IShopRepository shopRepository;
    @Autowired
    IMediaProductService mediaProductService;
    @Autowired
    IMediaProductRepository mediaProductRepository;
    @Autowired
    IOptionService optionService;
    @Autowired
    IShopService shopService;
    @Autowired
    IStockService stockService;
    @Autowired
    IValueService valueService;
    @Autowired
    ICartService cartService;
    @Autowired
    ICartRepository cartRepository;
    @Autowired
    IOrderRepository orderRepository;
    @Autowired
    VariantMapper variantMapper;
    @Autowired
    ITrackingService trackingService;
    @Autowired
    ITrackingRepository trackingRepository;
    @Autowired
    LittleProductMapper littleProductMapper;
    @Autowired
    ProductSpecification productSpecification;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    IAccountRepository accountRepository;
    @Autowired
    ISearchHistoryRepository searchHistoryRepository;
    @Autowired
    IAccountService accountService;
    @Autowired
    private OrderService orderService;

    @Override
    public ProductDetailForEditResponse findByIdForEdit(Long idProduct) {
        try {
            ProductDetailForEditResponse productDetailForEditResponse = new ProductDetailForEditResponse();
//            List<OptionResponse> optionResponseList = optionService.findByProductId(idProduct);
            List<OneParentOptionResponse> optionResponseList = optionService.findListParentResponse(idProduct);
            productDetailForEditResponse.setOption(optionResponseList);

            ProductResponse productResponse = findById(idProduct);
            productDetailForEditResponse.setProduct(productResponse);


            return productDetailForEditResponse;

        } catch (Exception e) {
            System.out.println("Loi khong the lay san pham chi tiet de chinh sua (product service) " + e);
            return null;
        }
    }

    @Override
    public ProductResponse findById(Long id) {
        try {
            ProductEntity productEntity = productRepository.findById(id).orElse(null);
            if (productEntity == null)
                return null;
            return productMapper.convertToResponse(productEntity);
        } catch (Exception e) {
            System.out.println("Loi tim product theo id (product service): " + e);
            return null;
        }
    }

    @Override
    public Page<LittleProductResponse> getBestSeller(Pageable pageable) {
        try {
//            Page<ProductEntity> result = productRepository.getBestSeller(pageable);
//            if (result == null)
//                return null;
//            return result.map(it -> littleProductMapper.convertToResposne(it));

            Specification<ProductEntity> productSpe = Specification
                    .where(productSpecification.sortBy("sellest"))
                    .and(productSpecification.defaultFind());

            Page<ProductEntity> product = productRepository.findAll(productSpe, pageable);
            if (product == null)
                return null;
            return product.map(it -> littleProductMapper.convertToResposne(it));



        } catch (Exception e) {
            System.out.println("Loi tim product theo id (product service): " + e);
            return null;
        }
    }

    @Override
    public Page<LittleProductResponse> getAllNews(Pageable pageable) {
        try {
//            Page<ProductEntity> result = productRepository.getAllNews(pageable);
//            if (result == null)
//                return null;
//            return result.map(it -> littleProductMapper.convertToResposne(it));

            Specification<ProductEntity> productSpe = Specification
                    .where(productSpecification.sortBy("popular"))
                    .and(productSpecification.defaultFind());

            Page<ProductEntity> product = productRepository.findAll(productSpe, pageable);
            if (product == null)
                return null;
            return product.map(it -> littleProductMapper.convertToResposne(it));
        } catch (Exception e) {
            System.out.println("Loi tim product theo id tat ca (product service): " + e);
            return null;
        }
    }

    @Override
    public Page<ProductResponse> findAllCustom(SearchProduct searchProduct, Pageable pageable) {
        try {
            return productRepository.findAllCustom(searchProduct.getKeySearch(), searchProduct.getCategoryName(),
                            searchProduct.getStartPrice(), searchProduct.getStartPrice(), searchProduct.getActive(), pageable)
                    .map(it -> productMapper.convertToResponse(it));
        } catch (Exception e) {
            System.out.println("Lỗi không thể hiển thị sản phẩm (product service): " + e);
            return null;
        }
    }

    @Override
    public Page<ProductResponse> findAllSpecCustom(SearchProduct searchProduct, Pageable pageable) {
        try {
            Specification<ProductEntity> productSpe = Specification
//                    .where(productSpecification.isActive(searchProduct.getActive()))
//                    .and(productSpecification.hasKeySearch(searchProduct.getKeySearch()))
                    .where(productSpecification.hasKeySearch(searchProduct.getKeySearch()))

//                    .and(ProductSpecification.hasCategoryName(searchProduct.getCategoryName()))
                    .and(productSpecification.hasStartPrice(searchProduct.getStartPrice()))
                    .and(productSpecification.hasEndPrice(searchProduct.getEndPrice()))
                    .and(productSpecification.hasCategoryId(searchProduct.getIdCategory()))
                    .and(productSpecification.hasVoting(searchProduct.getRating()))
                    .and(productSpecification.hasLocation(searchProduct.getPlace()))
                    .and(productSpecification.defaultFind())
                    .and(productSpecification.sortBy(searchProduct.getSortBy()));
            if (searchProduct.getKeySearch() != null && !searchProduct.getKeySearch().isEmpty()) {
                saveSearchHistory(searchProduct.getKeySearch());
            }
            Page<ProductEntity> product = productRepository.findAll(productSpe, pageable);
            if (product == null)
                return null;

            return product.map(it -> productMapper.convertToResponse(it));

            //------------------giới hạn sản phẩm bởi shop---------------------
            //Tạo 1 map <id-shop; các sản phẩm thỏa mãn>
//            Map<Long, List<ProductEntity>> productByShop = product.getContent().stream()
//                    .collect(Collectors.groupingBy(it -> it.getShop().getId()));

            //<lấy sản phẩm random>

            //-----------lấy chỉ số min, max để lặp
//            int minIndex = 0;
//            Map.Entry<Long, List<ProductEntity>> maxShopProduct = productByShop.entrySet().stream()
//                    .max(Comparator.comparingInt(entry -> entry.getValue().size()))
//                    .orElseThrow(()-> new NoSuchElementException("Khong tim thay san pham nao"));
//            int maxIndex = maxShopProduct.getValue().size();
//
//            List<ProductEntity> productRandom  =new ArrayList<>();
//            for(int i = minIndex; i < maxIndex; i+= 2){
//
//                for(var it: productByShop.entrySet()){
//                    int maxOfElement = it.getValue().size();
//
//                    if(i > maxOfElement)
//                        continue;
//                    List<ProductEntity> tmp = it.getValue().subList(i, Math.min(maxOfElement, i + 2));
//                    productRandom.addAll(tmp);
//                }
//            }
//
//            List<ProductResponse> productResponses = productRandom.stream()
//                    .map(productMapper::convertToResponse)
//                    .collect(Collectors.toList());

            //------------------ Phân trang lại ---------------------
//            int totalPages = (int) Math.ceil((double) productRandom.size() / pageable.getPageSize());
//            int totalElements = productRandom.size();

            // Tạo một Page mới với dữ liệu phân trang
//            return new PageImpl<>(productResponses, pageable, totalElements);


            //------------------ending giới hạn sản phẩm bởi shop---------------------


//            return product.map(it -> productMapper.convertToResponse(it));


        } catch (IndexOutOfBoundsException nullE) {
            System.out.println("Loi khi tim kiem specification prduct(product ervice - index out of): " + nullE);
            return null;
        } catch (Exception e) {
            System.out.println("Loi khi tim kiem specification prduct(product ervice): " + e);
            return null;
        }
    }

    private void saveSearchHistory(String keyword) {
        try {
            String email = null;
            try {
                email = jwtProvider.getEmailContext();
            } catch (Exception jwtException) {
                email = null;
            }
            Optional<Account> accountOptional = email != null ? accountRepository.findByEmail(email) : Optional.empty();
            Account account = accountOptional.orElse(null);

            if (account != null) {
                LocalDateTime vietNamTimeNow = UtilsFunction.getVietNameTimeNow();
                SearchHistory searchHistory = new SearchHistory();
                searchHistory.setAccId(account.getId());
                searchHistory.setKeyword(keyword);
                searchHistory.setCreatedAt(vietNamTimeNow);
                searchHistoryRepository.save(searchHistory);
            }
        } catch (Exception e) {
            System.out.println("Failed to save search history: " + e.getMessage());
        }
    }

    @Override
    public Page<ProductDetailForEditResponse> findAllSpecCustomForShop(SearchProduct searchProduct, Pageable pageable) {
        try {
            Specification<ProductEntity> productSpe = Specification
                    .where(productSpecification.hasKeySearch(searchProduct.getKeySearch()))
                    .and(productSpecification.hasShop(searchProduct.getShopId()))
                    .and(productSpecification.hasCategoryName(searchProduct.getCategoryName()))
                    .and(productSpecification.hasStartPrice(searchProduct.getStartPrice()))
                    .and(productSpecification.hasEndPrice(searchProduct.getEndPrice()))
                    .and(productSpecification.hasStatus(searchProduct.getStatus()))
                    .and(productSpecification.hasDefaultFindShop());

            Page<ProductEntity> product = productRepository.findAll(productSpe, pageable);
            if (product == null)
                return null;
            return product.map(it -> productMapper.convertToProductDetailForEditResponse(it));
        } catch (Exception e) {
            System.out.println("Loi tim kiem san pham theo shop(product service): " + e);
            return null;
        }
    }

    @Override
    public Boolean checkExits(Long id) {
        Boolean exits = productRepository.existsById(id);
        return exits;
    }

    @Override
    public Long getQuantityOfProduct(String op1, String vl1, String op2, String vl2) {
        try {

            return 1L;
        } catch (Exception e) {
            System.out.println("Khong the lay so luong cua san pham (product service): " + e);
            return 0L;
        }
    }

    @Override
    public ApiResponse<Long> save(ProductAddRequest request) {
        request.simpleValidate();


        ProductEntity productEntitySave = productMapper.convertToEntity(request);

        productEntitySave.setIsDelete(false);
        productEntitySave.setIsActive(true);
        productEntitySave.setIsBan(false);

        Long idProduct = productRepository.save(productEntitySave).getId();

        if (idProduct == null || idProduct == 0)
            throw new AppException(ErrorCode.CAN_NOT_SAVE_PRODUCT);

        ApiResponse<String> saveMedia = saveMediaProduct(request, idProduct);
        if (saveMedia.getCode() != 200)
            return new ApiResponse<>(saveMedia.getCode(), saveMedia.getMessage(), null);
//            return saveMedia;

        return ApiResponse.<Long>builder()
                .code(200)
                .message("Thêm sản phẩm thành công")
                .data(idProduct)
                .build();
    }

    @Override
    public ApiResponse<String> update(ProductAddRequest request) {
        request.simpleValidate();
        if (request == null || request.getId() == null)
            throw new AppException(ErrorCode.PRODUCT_REQUEST_NULL);

        //---------------thêm sản phẩm
        ProductEntity productEntity = productRepository.findById(request.getId()).orElse(null);
        if (productEntity == null)
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUNT);

        productEntity.setProductName(request.getProductName());
        productEntity.setProductDescription(request.getProductDescription());

        ProductEntity productEntitySave = productRepository.save(productEntity);
        if (productEntitySave == null)
            return new ApiResponse<>(400, "Không thể chỉnh sửa sản phẩm", null);
        else {
            System.out.println("Sửa thông tin sản phẩm ok");
        }

        //----------------xóa media cũ
        deleteMediaByProductId(productEntity.getId());


        //-----------------thêm media mới
        saveMediaProduct(request, request.getId());

        return new ApiResponse<>(200, "Update sản phẩm thành công", null);
    }


    //---update product
    //---khi update product, đồng thời update thêm cart và order
    @Override
    public ApiResponse<String> updateOneRequest(ProductEditRequest request) {
        request.simpleValidate();

        if (request == null || request.getId() == null)
            throw new AppException(ErrorCode.PRODUCT_REQUEST_NULL);

        //---ảnh và video không được rỗng
        if ( request.getDetailImage() == null || request.getCoverImage() == null
                || request.getDetailImage().size() == 0)
            throw new AppException(ErrorCode.PRODUCT_NOT_HAS_MEDIA);

        //1.   ---------------thêm thông tin cơ bản của sản phẩm
        ProductEntity productEntity = productRepository.findById(request.getId()).orElse(null);
        if (productEntity == null)
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUNT);

        //--nếu sản phẩm không phải của shop mình => fail
        Account account = accountService.getMyAccount();
        var shop = shopRepository.findByEmail(account.getEmail());
        if (shop == null)
            throw new AppException(ErrorCode.CANNOT_FIND_SHOP_USER);

        var myShop = shop.getId();
        if (myShop != productEntity.getShop().getId())
            throw new AppException(ErrorCode.PRODUCT_IS_NOT_YOUR_SHOP);
        //----

        productEntity.setProductName(request.getProductName());
        productEntity.setProductDescription(request.getProductDescription());

        ProductEntity productEntitySave = productRepository.save(productEntity);
        if (productEntitySave == null)
            return new ApiResponse<>(400, "Không thể chỉnh sửa sản phẩm", null);
        else {
            System.out.println("Sửa thông tin sản phẩm ok");
        }

        //-2. Xử lý thông tin media ---------------xóa media cũ
        deleteMediaByProductId(productEntity.getId());
        ApiResponse<Shop> shopApi = shopService.findMyShop();
        if (shopApi.getCode() != 200)
            return new ApiResponse<>(shopApi.getCode(), shopApi.getMessage(), null);

        //.....thêm media mới
//        ProductAddRequest addRequest = new ProductAddRequest(request.getId(), request.getProductName(), request.getProductDescription(),
//                request.getDetailImage(), request.getIntroVideo(), request.getCoverImage(), request.getCategoryId(), shopApi.getData().getId());
//        saveMediaProduct(addRequest, request.getId());

        saveSrcMediaProduct(request.getCoverImage(), request.getDetailImage(), request.getIntroVideo(), request.getId());

        //3. -----------------Xử lý stock
        Long idProductSave = productEntitySave.getId();
        ManyStockRequest manyStockRequest = request.getStocks();
        manyStockRequest.setIdProduct(idProductSave);

        ApiResponse<String> stockAPI = stockService.update(manyStockRequest);

        if (stockAPI.getCode() != 200)
            return new ApiResponse<>(stockAPI.getCode(), stockAPI.getMessage(), null);

        //4.----------------thêm image cho option
//        for (var it: request.getImageInfo()){
//            valueService.updateValueImage(it, idProductSave);
//        }

        for (var it : request.getStocks().getImageInfo()) {
            valueService.updateValueImage(it, idProductSave);
        }

        //5,-------------update cart
//        cartService.updateCartByProduct(request.getId());
        updateCart(productEntitySave.getId(), true, true);
        updateOrder(productEntitySave.getId(), true, true);

        return new ApiResponse<>(200, "Cập nhật sản phẩm thành công", null);
    }

    @Override
    public ApiResponse<String> hidden(ProductKeyRequest productKeyRequest) {
        ProductEntity productEntity = productRepository.findById(productKeyRequest.getProductId()).orElse(null);
        if (productEntity == null)
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUNT);
        productEntity.setIsActive(false);

        try {
            ProductEntity productEntitySave = productRepository.save(productEntity);

            updateCart(productEntitySave.getId(), false, true);
            return new ApiResponse<>(200, "Ẩn sản phẩm thành công", null);
        } catch (Exception e) {
            System.out.println("Loi khi xoa san pham: " + e);
            return new ApiResponse<>(400, "Không thể ẩn sản phẩm", null);
        }
    }

    @Override
    public ApiResponse<String> active(ProductKeyRequest productKeyRequest) {
        ProductEntity productEntity = productRepository.findById(productKeyRequest.getProductId()).orElse(null);
        if (productEntity == null)
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUNT);
        productEntity.setIsActive(true);


        try {
            ProductEntity productEntitySave = productRepository.save(productEntity);

            updateCart(productEntitySave.getId(), false, true);
            updateOrder(productEntitySave.getId(), false, true);
            return new ApiResponse<>(200, "Mở khóa sản phẩm thành công", null);
        } catch (Exception e) {
            System.out.println("Loi khi mo khoa san pham: " + e);
            return new ApiResponse<>(400, "Không thể mở khóa sản phẩm", null);
        }

    }

    @Override
    public ApiResponse<String> delete(ProductKeyRequest productKeyRequest) {
        ProductEntity productEntity = productRepository.findById(productKeyRequest.getProductId()).orElse(null);
        if (productEntity == null)
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUNT);
        productEntity.setIsDelete(true);

        try {
            ProductEntity productEntitySave = productRepository.save(productEntity);

            updateCart(productEntitySave.getId(), false, true);
            updateOrder(productEntitySave.getId(), false, true);

            return new ApiResponse<>(200, "Xóa sản phẩm thành công", null);
        } catch (Exception e) {
            System.out.println("Loi khi xoa san pham: " + e);
            return new ApiResponse<>(400, "Không thể xóa sản phẩm", null);
        }
    }

    @Override
    public List<Long> getAllReport(Long idShop, LocalDate start, LocalDate end) {
        try {
            List<Long> product = productRepository.getAllReportByDay(idShop, start, end);
            return product;
        } catch (Exception e) {
            System.out.println("Loi lay danh sach san pham bi report(product service): " + e);
            return null;
        }
    }

    @Override
    public List<ProductEntity> getAllOutStock(Long idShop) {
        try {
            List<ProductEntity> result = productRepository.getAllProductOutStock(idShop);
            return result;
        } catch (Exception e) {
            System.out.println("Khong tim thay so san pham het hang (product service): " + e);
            return null;
        }
    }


    @Override
    public void updateCart(Long idProduct, Boolean updateVariant, Boolean updateStatus) {
        List<CartEntity> cartList = cartRepository.findByProductId(idProduct);
        ProductEntity entity = productRepository.findById(idProduct).orElse(null);
        if (entity == null)
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUNT);

        //---chỉ xử lý cart nếu là case NORMAL(bình thường) hoặc HIDDEN_PRODDUCT (bị ẩn)


        for (var it : cartList) {

            //---Giỏ hảng chỉ có thể thay đổi trạng thái nếu
            //(nó đang bị ẩn sản phẩm hoặc trạng thái sản phẩm bị ẩn)
            Boolean checkContinue = it.getStatus().name().equalsIgnoreCase(StatusCartEnum.NORMAL.name()) ||
                    it.getStatus().name().equalsIgnoreCase(StatusCartEnum.HIDDEN_PRODDUCT.name());
            if (!checkContinue)
                continue;


            //----check update biến thể
            if (updateVariant == true) {
                //a, --kiểm tra biến thể

                List<VariantDTO> var = variantMapper.convertStringToDTO(it.getVariant());
                List<Long> ids = stockService.findIdsStockByVariants(var, idProduct);

                //----nếu không còn biến thể này => cập nhật trạng thái cart
                if (ids == null || ids.size() != 1) {
                    it.setStatus(StatusCartEnum.WAS_DELETD);
                }
            }

            //----check update status
            //-----nếu mà status đã là was_deleted => ignore
            //---nếu giỏ hàng bị xóa => ignore
            if (!checkContinue)
                continue;

            //b, kiểm tra status sản phẩm
            if (updateStatus == true) {
                System.out.println("start status : " + it.getVariant() + ", " + it.getStatus());
                Boolean isActive = entity.getIsActive(), isDelete = entity.getIsDelete(), isBan = entity.getIsBan();
                if (isActive != null && isActive == false) {
                    it.setStatus(StatusCartEnum.HIDDEN_PRODDUCT);
                }

                if (isBan != null && isBan == true) {
                    it.setStatus(StatusCartEnum.BAN_PRODUCT);
                }

                if (isDelete != null && isDelete == true) {
//                    System.out.println("In here");
                    it.setStatus(StatusCartEnum.WAS_DELETD);
                }

                //---nếu shop bị ban => ban luôn
                if (entity.getShop() != null) {
                    if (entity.getShop().getIsApproved() == false) {
                        it.setStatus(StatusCartEnum.BAN_PRODUCT);
                    }
                }

                //---nếu trạng thái là active, và sản phẩm chỉ bị ẩn thì mới có thể active lại
                var statusCart = it.getStatus().name();
                Boolean checkCanActive = statusCart.equalsIgnoreCase(StatusCartEnum.HIDDEN_PRODDUCT.name());
                if ((isActive == null || isActive == true) && checkCanActive) {
                    it.setStatus(StatusCartEnum.NORMAL);
                }

            }

            //---lưu lại cart sau khi update
            cartRepository.save(it);
        }
    }

    @Override
    public void updateOrder(Long idProduct, Boolean updateVariant, Boolean updateStatus) {
        List<OrderEntity> orderList = orderRepository.findOrderPenddingByIdProduct(idProduct);
        ProductEntity entity = productRepository.findById(idProduct).orElse(null);
        if (entity == null)
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUNT);

        for (var order : orderList) {
            //---chỉ xử lý nếu nó đang pending
            Boolean checkPending = order.getStatusOrder().name().equalsIgnoreCase(StatusOrderEnum.PENDING.name());
            if (!checkPending)
                continue;

            //---lấy ra tracking
            TrackingEntity track = trackingRepository.findByOrderId(order.getId());

            //---nếu update variant
            if (updateVariant == true) {

                //a, --kiểm tra biến thể
                //tìm trong order details (chỉ kiểm tra product đang update)
                if (order.getOrderDetail() != null) {
                    for (var ordDetail : order.getOrderDetail()) {
                        //---nếu không phải => bỏ qua vòng lặp
                        if (ordDetail.getProduct().getId() != idProduct)
                            continue;

                        List<VariantDTO> variantDTOS = variantMapper.convertStringToDTO(ordDetail.getVariant());
                        List<Long> ids = stockService.findIdsStockByVariants(variantDTOS, idProduct);

                        if (ids == null || ids.size() != 1 && order.getStatusOrder().name().equalsIgnoreCase(StatusOrderEnum.PENDING.name())) {
                            order.setStatusOrder(StatusOrderEnum.CANCELED);

                            //---------------------------------------
                            try {
                                System.out.println("--------------------update stock-------------------------------");
                                orderService.updateStock(order);
                            }catch (Exception e){
                                System.out.println("Loi update so luong (product service): " + e.getMessage());
                            }

                            if (track != null) {
                                track.setStatus(StatusOrderEnum.CANCELED);
                                track.setNote("Sản phẩm này đã bị xóa (xóa biến thể)");
                                track.setUserUpdateStatus(TypeUserEnum.SHOP);
                            }

                            break;
                        }
                    }
                }
            }

            if (!checkPending)
                continue;

            if (updateStatus == true) {
                //chỉ xử lý nếu nó vẫn đang pending
                //--trong order (đang chứa sản phẩm này), check status mới)
                if (order.getStatusOrder().name().equalsIgnoreCase(StatusOrderEnum.PENDING.name())) {
                    Boolean isActive = entity.getIsActive(), isDelete = entity.getIsDelete(),
                            isBan = entity.getIsBan();

                    Boolean isChange = false;
                    String mess = null;

                    if (isActive != null && isActive == false) {
                        isChange = true;
                        mess = "Sản phẩm đã bị ẩn";
                    }

                    if (isBan != null && isBan == true) {
                        isChange = true;
                        mess = "Sản phẩm đã bị ban";
                    }

                    if (entity.getShop() != null) {
                        if (entity.getShop().getIsApproved() == false) {
                            isChange = true;
                            mess = "Shop đã bị ban";
                        }
                    }

                    if (isDelete != null && isDelete == true) {
                        isChange = true;
                        mess = "Sản phẩm đã bị xóa";
                    }

                    if (isChange == true) {
                        order.setStatusOrder(StatusOrderEnum.CANCELED);

                        //-----
                        try {

                            System.out.println("--------------------update stock-------------------------------");
                            System.out.println("id order = " + order.getId());
                            orderService.updateStock(order);
                        }catch (Exception e){
                            System.out.println("Loi update so luong (product service): " + e.getMessage());
                        }

                        if (track != null) {
                            track.setStatus(StatusOrderEnum.CANCELED);
                            track.setNote(mess);
                            track.setUserUpdateStatus(TypeUserEnum.SHOP);
                        }
                    }
                }
            }

            orderRepository.save(order);
            if (track != null) {
                trackingRepository.save(track);
            }
        }
    }

    @Override
    public void updateCartShop(Long idShop) {
        List<ProductEntity> entities = productRepository.findByShopId(idShop);
        for (var it : entities) {
            updateCart(it.getId(), false, true);
        }
    }

    @Override
    public void updateOrderShop(Long idShop) {
        List<ProductEntity> entities = productRepository.findByShopId(idShop);
        for (var it : entities) {
            updateOrder(it.getId(), false, true);
        }
    }

    @Override
    public List<ProductRecommendationResponse> getRecommendations(String keyword) {
        Pageable pageable = PageRequest.of(0, 5);
        return productRepository.findRecommendedProducts(keyword, pageable);
    }


    public ApiResponse<String> saveSrcMediaProduct(String imageCover, List<String> detailsImage, String video, Long idProduct) {
        ProductEntity product = new ProductEntity();
        product.setId(idProduct);

        MediaProductEntity mediaCover = new MediaProductEntity(null, imageCover, TypeMediaEnum.COVER_IMAGE.name(), product);
        mediaProductRepository.save(mediaCover);

        for (var it : detailsImage) {
            MediaProductEntity tmp = new MediaProductEntity(null, it, TypeMediaEnum.DETAIL_IMAGE.name(), product);
            mediaProductRepository.save(tmp);
        }
        MediaProductEntity mediaVideo = new MediaProductEntity(null, video, TypeMediaEnum.INTRO_VIDEO.name(), product);
        mediaProductRepository.save(mediaVideo);
        return new ApiResponse<>(200, "Lưu media cho product thành công", null);
    }

    public ApiResponse<String> saveMediaProduct(ProductAddRequest productRequest, Long idProduct) {
        if (productRequest == null)
            throw new AppException(ErrorCode.PRODUCT_REQUEST_NULL);

        //-----detailImage
        var sourceMedia = Const.FOLDER_MEDIA_PRODUCT;
//        UtilsFunction.createFolder(sourceMedia);
        if (productRequest.getDetailImage() != null) {
            for (var it : productRequest.getDetailImage()) {
                if (UtilsFunction.isImage(it.getOriginalFilename())) {
                    String saveImageDetail = UtilsFunction.saveMediaFile(it, sourceMedia);
                    if (saveImageDetail != null) {
                        saveImageDetail = Const.PREFIX_URL_MEDIA + saveImageDetail;
                        MediaProductRequest mediaProductRequest = new MediaProductRequest(null, saveImageDetail, TypeMediaEnum.DETAIL_IMAGE.name(), idProduct);
                        mediaProductService.save(mediaProductRequest);
                    }
                }
            }
        }

        //-----------video
        if (productRequest.getIntroVideo() != null) {
            MultipartFile video = productRequest.getIntroVideo();
            if (UtilsFunction.isVideo(video.getOriginalFilename())) {
                if (UtilsFunction.getMbSizeOfFile(video) > 30)
                    throw new AppException(ErrorCode.VIDEO_PRODUCT_LARGE);

//                String saveVideoIntro = UtilsFunction.saveMediaFile(video, video.getOriginalFilename() );
                String saveVideoIntro = UtilsFunction.saveMediaFile(video, sourceMedia);
                if (saveVideoIntro != null) {
                    saveVideoIntro = Const.PREFIX_URL_MEDIA + saveVideoIntro;
                    MediaProductRequest mediaProductRequest = new MediaProductRequest(null, saveVideoIntro, TypeMediaEnum.INTRO_VIDEO.name(), idProduct);
                    mediaProductService.save(mediaProductRequest);
                }
            }
        }

        //----------cover image
        if (productRequest.getCoverImage() != null) {
            MultipartFile coverImage = productRequest.getCoverImage();
            if (UtilsFunction.isImage(coverImage.getOriginalFilename())) {
//                String saveCoverImage = UtilsFunction.saveMediaFile(coverImage, coverImage.getOriginalFilename() );
                String saveCoverImage = UtilsFunction.saveMediaFile(coverImage, sourceMedia);
                if (saveCoverImage != null) {
                    saveCoverImage = Const.PREFIX_URL_MEDIA + saveCoverImage;
                    MediaProductRequest mediaProductRequest = new MediaProductRequest(null, saveCoverImage, TypeMediaEnum.COVER_IMAGE.name(), idProduct);
                    mediaProductService.save(mediaProductRequest);
                }
            }
        }

        return ApiResponse.<String>builder()
                .code(200)
                .message("Thêm ảnh cho sản phẩm thành công")
                .build();
    }

    public ApiResponse<String> deleteMediaByProductId(Long id) {
        try {
            List<MediaProductEntity> mediaProductEntityList = mediaProductRepository.findByProductId(id);
            for (var it : mediaProductEntityList) {
                mediaProductRepository.deleteById(it.getId());
            }
            return new ApiResponse<>(200, "Xóa media thành công", null);
        } catch (Exception e) {
            return new ApiResponse<>(400, "Lỗi khi xóa media", null);
        }
    }


}

//ReportServiceImpl
//OrderService
//OrderSchedule