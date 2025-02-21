package org.example.cy_shop.service.impl.order;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.example.cy_shop.configuration.jwtConfig.JwtAuthenticationFilter;
import org.example.cy_shop.configuration.jwtConfig.JwtProvider;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.cart.CartRequest;
import org.example.cy_shop.dto.request.cart.OptionCartRequest;
import org.example.cy_shop.dto.request.cart.update.CartUpdateRequest;
import org.example.cy_shop.dto.request.order.VariantDTO;
import org.example.cy_shop.dto.response.auth.AccountResponse;
import org.example.cy_shop.dto.response.cart.CartResponse;
import org.example.cy_shop.dto.response.cart.StockInfo;
import org.example.cy_shop.dto.response.product.stock_response.StockResponse;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.order.CartEntity;
import org.example.cy_shop.entity.product.StockEntity;
import org.example.cy_shop.entity.product.ValueEntity;
import org.example.cy_shop.enums.StatusCartEnum;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.order.CartMapper;
import org.example.cy_shop.mapper.order.VariantMapper;
import org.example.cy_shop.repository.order.ICartRepository;
import org.example.cy_shop.repository.product_repository.IProductRepository;
import org.example.cy_shop.repository.product_repository.IStockRepository;
import org.example.cy_shop.repository.product_repository.IValueRepository;
import org.example.cy_shop.service.IAccountService;
import org.example.cy_shop.service.order.ICartService;
import org.example.cy_shop.service.product.IMediaProductService;
import org.example.cy_shop.service.product.IStockService;
import org.example.cy_shop.service.product.IValueService;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CartService implements ICartService {
    @Autowired
    ICartRepository cartRepository;
    @Autowired
    CartMapper cartMapper;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    IAccountService accountService;
    @Autowired
    JwtAuthenticationFilter authenticationFilter;
    @Autowired
    IValueRepository valueRepository;
    @Autowired
    IStockRepository stockRepository;
    @Autowired
    IStockService stockService;
    @Autowired
    IValueService valueService;
    @Autowired
    IProductRepository productRepository;
    @Autowired
    IMediaProductService mediaProductService;
    @Autowired
    VariantMapper variantMapper;

    @Value("${jwt.SECRET_ACCESS_TOKEN_KEY}")
    private String JWT_SECRET_ACCESS_TOKEN;

    @Override
    public ApiResponse<Page<CartResponse>> findAllCustom(Pageable pageable) {
        try {
            Long accId = accountService.getMyAccount().getId();
            Page<CartEntity> cart = cartRepository.findAllCustome(accId, pageable);
            Page<CartResponse> cartResponses = cart.map(it -> cartMapper.convertToResponse(it));
            return new ApiResponse<>(200, "Danh sách giỏ hàng", cartResponses);
        }catch (Exception e) {

            System.out.println("Loi khi tim kiem gio hang (cart service): " + e);
            String err  = "Không thể load sản phẩm: " + e.getMessage();
            return new ApiResponse<>(400, err, null);
        }
    }

    @Override
    public String getImage(List<OptionCartRequest> option, Long idProduct) {
        for (var op: option){
            List <ValueEntity> valueEntity = valueRepository.existsByOptionNameAndOptionValueAndIdProduct
                    (op.getOptionName(), op.getOptionValue(), idProduct);
            for(var vl: valueEntity){
                if(vl.getImage() != null)
                    return vl.getImage();
            }
        }

        return mediaProductService.getCoverImageProduct(idProduct);
    }

    @Override
    public Long getQuantityByManyOption(String op1, String vl1, String op2, String vl2, Long idPrd) {
        List<Long> ids = valueService.findIdStockByManyOptionAndValue(op1, vl1, op2, vl2, idPrd);
        System.out.println("size = " + ids);
        if(ids == null){
            List<StockResponse> stockRes = stockService.findByProductId(idPrd);
            if(stockRes.size() == 1)
                return stockRes.get(0).getQuantity();
        }
        if(ids.size() == 1) {
            StockResponse stockResponse = stockService.findById(ids.get(0));
//            System.out.println("quantity in here");
//            System.out.println(stockResponse.getQuantity());
            return stockResponse.getQuantity();
        }

        System.out.println("Loi khi lay so luong cua san pham");
        return 0L;
    }

    @Override
    public ApiResponse<Long> add(CartRequest cartRequest) {

        //-----simple check
        if(cartRequest.getQuantity() == null || cartRequest.getQuantity() < 0)
            return new ApiResponse<>(ErrorCode.CART_REQUEST_INVALID.getCode(), ErrorCode.CART_REQUEST_INVALID.getMessage(), null);

        //---------------kiểm tra user
        ApiResponse<AccountResponse> accountResponse = getAccountSeller();
        if (accountResponse.getCode() != 200)
            return new ApiResponse<>(accountResponse.getCode(), accountResponse.getMessage(), null);

        Account account = new Account();

        if (accountResponse.getCode() == 200) {
            account.setId(accountResponse.getData().getId());
        }

        //---------------lấy ra số lượng và giá của (sản phẩm + biến thể)
        ApiResponse<StockInfo> stockAPI = getStockInfo(cartRequest);
        if(stockAPI.getCode() != 200)
            return new ApiResponse<>(stockAPI.getCode(), stockAPI.getMessage(), null);

        //chuyển đổi => đồng thới validate option name
        CartEntity cartEntityToSave = cartMapper.convertToEntity(cartRequest);

        //-----tìm kiếm, nxem trong cart đã có thực thể đó chưa
        List<VariantDTO> var1 = cartRequest.getOption();
        List<VariantDTO> var2 = new ArrayList<>();
        if(var1.size() == 2){
            var2.add(var1.get(1));
            var2.add(var1.get(0));
        }

        CartEntity cartEntityFind = cartRepository.findByProductIdAndVariantAndIdAccount(cartRequest.getIdProduct(),
                variantMapper.convertVariantToString(var1), account.getId());

        if(cartEntityFind == null)
            cartEntityFind = cartRepository.findByProductIdAndVariantAndIdAccount(cartRequest.getIdProduct(),
                    variantMapper.convertVariantToString(var2), account.getId());

        System.out.println("var1: " + var1.size() + ", " + variantMapper.convertVariantToString(var1));
        System.out.println("var2: " + var2.size() + ", " + variantMapper.convertVariantToString(var2));

        System.out.println("cart entity: " + cartEntityFind == null);
        System.out.println("has: " + cartEntityFind);

        if(cartEntityFind != null){
            //------số lượng trước đó và số lượng nếu thêm
            Long quantityTmp = cartEntityFind.getQuantity() + cartRequest.getQuantity();
            //--nếu số lượng > kho => false

            System.out.println("quan tity tmp: ");
            if(quantityTmp > stockAPI.getData().getQuantity())
                return new ApiResponse<>(ErrorCode.QUANTITY_BIGGER_THAN_STOCK.getCode(),
                        ErrorCode.QUANTITY_OF_CART_NOT_VALID.getMessage(), null);

            cartEntityFind.setQuantity(quantityTmp);
            cartEntityFind.setStatus(StatusCartEnum.NORMAL);
            CartEntity save = cartRepository.save(cartEntityFind);

            if (save != null)
                return new ApiResponse<>(200, "Thêm sản phẩm vào giỏ hàng thành công", save.getId());
        }

        //----nếu không có sản phẩm trong cart trước đó, lưu mới
        cartEntityToSave.setAccount(account);
        cartEntityToSave.setQuantity(cartRequest.getQuantity());
//        cartEntityToSave.setPrice(stockAPI.getData().getPrice());
        cartEntityToSave.setStatus(StatusCartEnum.NORMAL);



        CartEntity cartEntity = cartRepository.save(cartEntityToSave);

        if (cartEntity != null)
            return new ApiResponse<>(200, "Thêm sản phẩm vào giỏ hàng thành công", cartEntity.getId());

        return new ApiResponse<>(400, "Không thể lưu giỏ hàng", null);


    }

    @Override
    public ApiResponse<String> updateCartByProduct(Long idProduct) {
        try {
            //-----lấy ra list cart đang hoạt động
            List<CartEntity> cartEntityList = cartRepository.findByStatusAndProductId(StatusCartEnum.NORMAL, idProduct);

            for(var cart: cartEntityList){
                //---lấy ra biến thể (có thể có hoặc không có biến thể)
                List<OptionCartRequest> optionList = convertStringToOption(cart.getVariant());

                //-----nếu có biến thể
                if(optionList != null && optionList.size() > 0) {
                    List<Long> ids = getIdStockByManyOption(optionList, idProduct);
                    if (ids == null || ids.size() != 1) {
                        cart.setStatus(StatusCartEnum.WAS_DELETD);
                        cartRepository.save(cart);
                    }
                    if (ids.size() == 1) {
                        StockResponse stockResponse = stockService.findById(ids.get(0));
//                        cart.setPrice(stockResponse.getPrice());
                        cartRepository.save(cart);
                    }
                }
                //-----nếu không có biến thể
                //(tìm stock duy nhất của sản phẩm)
                else{
                    List<StockResponse> stockTmp = stockService.findByProductId(idProduct);
                    if(stockTmp != null && stockTmp.size() == 1){
//                        cart.setPrice(stockTmp.get(0).getPrice());
                        cartRepository.save(cart);
                    }else {
                        cart.setStatus(StatusCartEnum.WAS_DELETD);
                        cartRepository.save(cart);
                    }
                }
            }


            return new ApiResponse<>(200, "Update giỏ hàng thành công", null);
        }catch (Exception e){
            return new ApiResponse<>(400, "Không thể cập nhật giỏ hàng", e.getMessage());
        }

    }

    @Override
    public ApiResponse<Long> updateCart(CartUpdateRequest request) {
        CartEntity cartEntity = cartRepository.findById(request.getId()).orElse(null);
        if(cartEntity == null)
            throw new AppException(ErrorCode.CART_NOT_FOUND);

//        cartEntity.set
        cartEntity.setQuantity(request.getQuantity());
        cartEntity.setVariant(variantMapper.convertVariantToString(request.getVariants()));

        StockInfo stockInfo = stockService.getStockInfo(request.getVariants(), cartEntity.getProductId());
        if(stockInfo == null)
            throw new AppException(ErrorCode.STOCK_OF_PRODUCT_NOT_FOUND);
        if(stockInfo.getQuantity() < request.getQuantity())
            return new ApiResponse<>(400, "Số lượng thêm vào lớn hơn số lượng trong kho", null);

        CartEntity cartSave = cartRepository.save(cartEntity);
        if(cartSave == null || cartSave.getId() == null)
            throw new AppException(ErrorCode.CANNOT_SAVE_CART);

        return new ApiResponse<>(200, "Cập nhật giỏ hàng thành công", cartSave.getId());
    }

    List<Long> getIdStockByManyOption(List<OptionCartRequest> requests, Long idPrd){
        String op1 = null, vl1 = null, op2 = null, vl2 = null;
        if(requests.size() == 1){
            op1 = requests.get(0).getOptionName();
            vl1 = requests.get(0).getOptionValue();
        }
        if(requests.size() == 2){
            op1 = requests.get(0).getOptionName();
            vl1 = requests.get(0).getOptionValue();
            op2 = requests.get(1).getOptionName();
            vl2 = requests.get(1).getOptionValue();
        }
        return valueService.findIdStockByManyOptionAndValue(op1, vl1, op2, vl2, idPrd);
    }

    @Override
    public ApiResponse<String> deleteById(Long idCart) {
        try {
            cartRepository.deleteById(idCart);
            return new ApiResponse<>(200, "Xóa sản phẩm khỏi giỏ hàng thành công", null);
        }catch (Exception e){
            return new ApiResponse<>(400, "Xóa sản phẩm thất bại", e.getMessage());
        }
    }

    //--------------------------function utils
    ApiResponse<AccountResponse> getAccountSeller() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = authenticationFilter.getJwtFromRequest(request);
            boolean isLogin = authenticationFilter.validateToken(JWT_SECRET_ACCESS_TOKEN, token);
            if (isLogin == false)
                return new ApiResponse<>(ErrorCode.USER_NOT_LOGIN.getCode(), ErrorCode.USER_NOT_LOGIN.getMessage(), null);

            String email = jwtProvider.getEmailContext();

            System.out.println("email = " + email);

            AccountResponse accountResponse = accountService.findByEmail(email);
            if (email == null)
                return new ApiResponse<>(ErrorCode.USER_NOT_LOGIN.getCode(), ErrorCode.USER_NOT_LOGIN.getMessage(), null);
            if (accountResponse == null)
                return new ApiResponse<>(ErrorCode.USER_NOT_FINDING.getCode(), ErrorCode.USER_NOT_FINDING.getMessage(), null);
//                return new ApiResponse<>(400, "Không tìm thấy tài khoản", null);
            if (!accountResponse.getRole().contains(Const.ROLE_USER))
                return new ApiResponse<>(ErrorCode.USER_NOT_HAS_ROLE_BUY.getCode(), ErrorCode.USER_NOT_HAS_ROLE_BUY.getMessage(), null);

            return new ApiResponse<>(200, "Thông tin tài khoản", accountResponse);
        } catch (Exception e) {
            System.out.println("Khong the lay tai khoan user(cart service): " + e);
            return new ApiResponse<>(400, "Lỗi không thể lấy tài khoản user", null);
        }
    }

    //-----------kiem tra quantity (neu dung thi tra ra price)
    ApiResponse<StockInfo> getStockInfo(CartRequest cartRequest){

        //nếu request không chọn biến thể
        if(cartRequest.getOption() == null || cartRequest.getOption().size() == 0){

            //-----tìm  option xem có tồn tại không
            List<ValueEntity> valueEntity = valueRepository.findByIdProduct(cartRequest.getIdProduct());
            //-----nếu có biến thể thì => false (vì trường hợp này không truyền biến thể)
            if(valueEntity != null || valueEntity.size() > 0){
                return new ApiResponse<>(ErrorCode.SELECT_OPTION_OF_PRODUCT_NOT_VALID.getCode(),
                        ErrorCode.SELECT_OPTION_OF_PRODUCT_NOT_VALID.getMessage(), null);
            }

            //---lấy ra stock
            List<StockEntity> stockEntitity = stockRepository.findByIdProduct(cartRequest.getIdProduct());
            //------nếu có nhiều stock hoặc null => lỗi  (vì stock chỉ được có 1 )
            if(stockEntitity == null || stockEntitity.size() != 1)
                return new ApiResponse<>(ErrorCode.STOCK_OF_PRODUCT_NOT_FOUND.getCode(),
                        ErrorCode.STOCK_OF_PRODUCT_NOT_FOUND.getMessage(), null);
            Long quantity = stockEntitity.get(0).getQuantity();
            Double price = stockEntitity.get(0).getPrice();
            return new ApiResponse<>(200, "Số lượng phù hợp", new StockInfo(null,quantity, price, null));
        }

        //nếu request  có chọn biến thể
        else {
            String op1 = null, vl1 = null, op2 = null, vl2 = null;
            if(cartRequest.getOption().size() == 1){
                op1 = cartRequest.getOption().get(0).getOptionName();
                vl1 = cartRequest.getOption().get(0).getOptionValue();
            }

            if(cartRequest.getOption().size() == 2){
                op1 = cartRequest.getOption().get(0).getOptionName();
                vl1 = cartRequest.getOption().get(0).getOptionValue();
                op2 = cartRequest.getOption().get(1).getOptionName();
                vl2 = cartRequest.getOption().get(1).getOptionValue();
            }

            //------có nhiều hơn 2 biến thể => false
            if(cartRequest.getOption().size() > 2){
                return new ApiResponse<>(ErrorCode.OPTION_OF_CART_REQUEST_NOT_VALID.getCode(),
                        ErrorCode.OPTION_OF_CART_REQUEST_NOT_VALID.getMessage(), null);
            }

            List<Long> ids = valueService.findIdStockByManyOptionAndValue(op1, vl1, op2, vl2, cartRequest.getIdProduct());

            //-----nếu khng thấy idStock nào => false
            if(ids ==null || ids.size() == 0)
                return new ApiResponse<>(ErrorCode.OPTION_OF_CART_REQUEST_NOT_VALID.getCode(),
                        ErrorCode.OPTION_OF_CART_REQUEST_NOT_VALID.getMessage(), null);

            //=====nếu có nhiều stock => chưa chọn hết biến thể
            if(ids.size() != 1)
                return new ApiResponse<>(ErrorCode.OPTION_OF_CART_REQUEST_NOT_FULL.getCode(),
                        ErrorCode.OPTION_OF_CART_REQUEST_NOT_FULL.getMessage(), null);

            //====> trường hợp đúng
            Long idStock = ids.get(0);
            StockEntity stockEntity = stockRepository.findById(idStock).orElse(null);
            if(stockEntity == null)
                return new ApiResponse<>(ErrorCode.STOCK_OF_PRODUCT_NOT_FOUND.getCode(),
                        ErrorCode.STOCK_OF_PRODUCT_NOT_FOUND.getMessage(), null);
            Long  quantity = stockEntity.getQuantity();
            Double price = stockEntity.getPrice();

            return new ApiResponse<>(200, "Số lượng phù hợp", new StockInfo(null,quantity, price, null));
        }
    }

    @Override
     public List<OptionCartRequest>convertStringToOption(String str){
        List<OptionCartRequest> optionList = new ArrayList<>();
        if(str != null)
            str = str.trim();
        String[] lines = str.split("\n");

        for(var it: lines){
            String[] paths = it.split(Const.REGEX_SEPARATOR);

            if(paths.length == 2) {
                OptionCartRequest tmp = new OptionCartRequest(paths[0], paths[1]);
                optionList.add(tmp);
            }
        }
        return optionList;
    }


    @Override
    public String convertOptionToString(List<OptionCartRequest> option){
        String variant = "";

        if(option != null){
            for(var it: option){
                variant = variant + it.getOptionName() + Const.SEPARATOR + it.getOptionValue() + "\n";
            }
        }

        return variant;
    }
}

//ProductService