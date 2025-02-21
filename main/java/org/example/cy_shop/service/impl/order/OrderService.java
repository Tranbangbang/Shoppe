package org.example.cy_shop.service.impl.order;

import jakarta.transaction.Transactional;
import org.example.cy_shop.configuration.jwtConfig.JwtProvider;
import org.example.cy_shop.controller.client.chat.ClientChattingController;
import org.example.cy_shop.controller.client.product.ClientProductController;
import org.example.cy_shop.controller.seller.statics.StaticsShopController;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.notification.NotificationRequest;
import org.example.cy_shop.dto.request.order.OrderKeyDTO;
import org.example.cy_shop.dto.request.order.vnpay.OrderIdsRequest;
import org.example.cy_shop.dto.request.search.SearchOrder;
import org.example.cy_shop.dto.response.order.OrderShopResponse;
import org.example.cy_shop.dto.request.order.OrderKeyRequest;
import org.example.cy_shop.dto.request.order.add.OrderDetailsRequest;
import org.example.cy_shop.dto.request.order.add.OrderRequest;
import org.example.cy_shop.dto.response.order.voucher.VoucherForOrderResponse;
import org.example.cy_shop.dto.response.shop.ShopRevenueDTO;
import org.example.cy_shop.dto.response.cart.StockInfo;
import org.example.cy_shop.dto.response.order.StaticOrderResponse;
import org.example.cy_shop.dto.response.order.shop.OrderOfShopResponse;
import org.example.cy_shop.entity.*;
import org.example.cy_shop.entity.order.OrderDetailEntity;
import org.example.cy_shop.entity.order.OrderEntity;
import org.example.cy_shop.entity.order.TrackingEntity;
import org.example.cy_shop.entity.product.ProductEntity;
import org.example.cy_shop.entity.product.StockEntity;
import org.example.cy_shop.enums.EnumDiscountType;
import org.example.cy_shop.enums.EnumNotificationStatus;
import org.example.cy_shop.enums.EnumTypeStatus;
import org.example.cy_shop.enums.order.StatusOrderEnum;
import org.example.cy_shop.enums.order.StatusPaymentEnum;
import org.example.cy_shop.enums.order.TypePaymenEnum;
import org.example.cy_shop.enums.user.TypeUserEnum;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.NotificationMapper;
import org.example.cy_shop.mapper.VoucherMapper;
import org.example.cy_shop.mapper.order.OrderMapper;
import org.example.cy_shop.mapper.order.VariantMapper;
import org.example.cy_shop.mapper.order.shop.OrderOfShopMapper;
import org.example.cy_shop.repository.INotificationRepository;
import org.example.cy_shop.repository.IShopRepository;
import org.example.cy_shop.repository.IAccountRepository;
import org.example.cy_shop.repository.IUserVoucherRepository;
import org.example.cy_shop.repository.order.IOrderDetailRepository;
import org.example.cy_shop.repository.order.IOrderRepository;
import org.example.cy_shop.repository.order.ITrackingRepository;
import org.example.cy_shop.repository.product_repository.IProductRepository;
import org.example.cy_shop.repository.product_repository.IStockRepository;
import org.example.cy_shop.service.IAccountService;
import org.example.cy_shop.service.impl.product.StockService;
import org.example.cy_shop.service.notification.NotificationService;
import org.example.cy_shop.service.order.IOrderService;
import org.example.cy_shop.service.product.IProductService;
import org.example.cy_shop.specification.OrderShopSpecification;
import org.example.cy_shop.utils.UtilsFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService implements IOrderService {
    @Autowired
    IAccountService accountService;
    @Autowired
    IOrderRepository orderRepository;
    @Autowired
    IOrderDetailRepository orderDetailRepository;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    IUserVoucherRepository userVoucherRepository;
    @Autowired
    IProductService productService;
    @Autowired
    IProductRepository productRepository;
    @Autowired
    private StockService stockService;
    @Autowired
    private VoucherMapper voucherMapper;
    @Autowired
    IStockRepository stockRepository;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    IAccountRepository accountRepository;
    @Autowired
    ITrackingRepository trackingRepository;
    @Autowired
    IShopRepository shopRepository;
    @Autowired
    OrderOfShopMapper orderOfShopMapper;
    @Autowired
    VariantMapper variantMapper;
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    NotificationMapper notificationMapper;
    @Autowired
    INotificationRepository notificationRepository;
    @Autowired
    NotificationService notificationService;

    //---lưu base order (1 đơn hàng (1 base order + 1 shop))
    @Override
    public ApiResponse<Long> addOneOrder(OrderRequest orderRequest) {
        // Kiểm tra request hợp lệ
        if (orderRequest == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        // Đảm bảo `idShop` trong `OrderRequest` là hợp lệ
        if (orderRequest.getOrderDetails() == null || orderRequest.getOrderDetails().isEmpty()) {
            throw new AppException(ErrorCode.ORDER_DETAILS_EMPTY);
        }

        // Lấy `idShop` từ orderDetails (đầu tiên)
        Long idShop = orderRequest.getOrderDetails().get(0).getIdShop();
        if (idShop == null) {
            throw new AppException(ErrorCode.SHOP_NOT_FOUND);
        }

        // Chuyển đổi `OrderRequest` thành `OrderEntity`
        OrderEntity orderEntity = orderMapper.convertToOrderEntity(orderRequest);
        orderEntity.setIdShop(idShop);

        // Lưu đơn hàng
        OrderEntity orderSave = orderRepository.save(orderEntity);
        if (orderSave == null || orderSave.getId() == null || orderSave.getId() == 0) {
            throw new AppException(ErrorCode.CANNOT_SAVE_ORDER);
        }

        // Ghi nhận lịch sử tracking cho đơn hàng
        TrackingEntity trackingEntity = new TrackingEntity(
                null,
                StatusOrderEnum.PENDING,
                null, // Ghi chú có thể thêm từ `orderRequest` nếu cần
                TypeUserEnum.USER,
                orderSave
        );
        TrackingEntity trackSave = trackingRepository.save(trackingEntity);
        if (trackSave == null || trackSave.getId() == null || trackSave.getId() == 0) {
            throw new AppException(ErrorCode.CANNOT_SAVE_TRACKING);
        }

        Account account = accountService.getMyAccount();
        if (account == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }

        try {
            String actorName = account.getUsername();
            String notificationContent = "Người dùng " + actorName + " đã đặt hàng mới.";
            NotificationRequest notificationRequest = new NotificationRequest();
            notificationRequest.setAccId(orderSave.getIdShop());
            notificationRequest.setActorId(account.getId());
            notificationRequest.setUsername("shop");
            notificationRequest.setActorName(actorName);
            notificationRequest.setType(EnumTypeStatus.order_status_changed);
            notificationRequest.setContent(notificationContent);
            notificationRequest.setStatus(EnumNotificationStatus.unseen);
            Notification notification = notificationMapper.toEntity(notificationRequest);
            notificationRepository.save(notification);
            notificationService.senNotification(orderSave.getIdShop().toString(), notification);
        } catch (Exception e) {
            System.out.println("Loi khi tao thong bao (order service): " + e);
        }

        return new ApiResponse<>(200, "Lưu order thành công", orderSave.getId());
    }


    //---lưu 1 shop (1 shop có nhiều sản pẩm)
    @Override
    public ApiResponse<OrderKeyDTO> addOneOrderDetails(OrderDetailsRequest request, Long orderId) throws InterruptedException {
        //---duyệt qua các sản phẩm
        OrderKeyDTO result = new OrderKeyDTO(request.getIdShop(), orderId);

        //----nếu user voucher null  => bỏ
        if (request.getIdUserVoucher() == null)
            request.setIdUserVoucher(0L);
        UserVoucher userVoucher = userVoucherRepository.findById(request.getIdUserVoucher()).orElse(null);

        //---giá sau cùng của đơn hàng
        Double priceOrder = 0.0;
        for (var it : request.getProductOrders()) {
            //---redis mua hàng
            String redisKey = "order" + it.getIdProduct();
            int timeout = 10;  // Thời gian chờ tối đa (giây)
            long startTime = System.currentTimeMillis();

//            if(redisTemplate.opsForValue().get(redisKey) != null)
//                throw new AppException(ErrorCode.PRODUCT_WAS_BUY_BY_ANOTHER);
            while (redisTemplate.opsForValue().get(redisKey) != null) {
                // Nếu đã đợi đủ thời gian timeout thì thoát
                if ((System.currentTimeMillis() - startTime) / 1000 > timeout) {
                    System.out.println("Thời gian chờ hết, không thể xử lý đơn hàng.");
                    throw new AppException(ErrorCode.ORDER_TIME_OUT);
                }
                // Chờ 500ms trước khi kiểm tra lại
                Thread.sleep(500);
            }
            // Đánh dấu sản phẩm đang được mua
            redisTemplate.opsForValue().set(redisKey, "locked");
            //-----redis mua hàng-----

            //---check thông tin sản phẩm và shop có match nhau không
            ProductEntity prd = productRepository.findById(it.getIdProduct()).orElse(null);
            //---kiểm tra thông tin shop v sản phẩm có khớp không
            if (prd == null)
                throw new AppException(ErrorCode.PRODUCT_NOT_FOUNT);
            if (!prd.getShop().getId().equals(request.getIdShop()))
                throw new AppException(ErrorCode.PRODUCT_AND_ID_SHOP_NOT_MATCH);

            //---nếu sản phẩm của shop mình => không cho mua
            if (checkPrdIsMyShop(prd) != null && checkPrdIsMyShop(prd) == true) {
                throw new AppException(ErrorCode.CANNOT_BUY_MY_SHOP);
            }

            //---lấy ra giá của sản phẩm, và số lượng còn lại trong kho
            StockInfo stockInfo = stockService.getStockInfo(it.getVariants(), it.getIdProduct());

            //convert sang orderDetailEntity
            //(thiếu giá, số lượng, khấu trừ voucher)
            OrderDetailEntity orderDetailEntity = orderMapper.convertToOrderDetailEntity(it, orderId);

            if (stockInfo == null) {
                throw new AppException(ErrorCode.STOCK_OF_PRODUCT_NOT_FOUND);
            }
            Long quantity = stockInfo.getQuantity();
            Double price = stockInfo.getPrice();

            if (it.getQuantity() > quantity) {
                throw new AppException(ErrorCode.QUANTITY_BIGGER_THAN_STOCK);
            }
            if (quantity == 0)
                throw new AppException(ErrorCode.QUANTITY_OF_STOCK_NOT_HAS);

            //---thêm vào đơn giá
            priceOrder += it.getQuantity() * price;
//
//                        //---cập nhật voucher
//                        userVoucher.setIsValid(false);
//                        userVoucherRepository.save(userVoucher);

            //---set thêm thông tin cho orderDetails
            orderDetailEntity.setQuantity(it.getQuantity());
            orderDetailEntity.setPrice(price);
            orderDetailEntity.setLastPrice(it.getQuantity() * price);

            orderDetailRepository.save(orderDetailEntity);

            //---cập nhật số lượng sản phẩm
            List<Long> idStocks = stockService.findIdsStockByVariants(it.getVariants(), it.getIdProduct());
            if (idStocks == null || idStocks.size() != 1)
                throw new AppException(ErrorCode.STOCK_OF_PRODUCT_NOT_FOUND);

            StockEntity stockEntity = stockRepository.findById(idStocks.get(0)).orElse(null);
            if (stockEntity == null)
                throw new AppException(ErrorCode.STOCK_OF_PRODUCT_NOT_FOUND);

            stockEntity.setQuantity(stockEntity.getQuantity() - it.getQuantity());
            stockRepository.save(stockEntity);

            //---giải phóng key
//            System.out.println("Doi mua hang");
//            Thread.sleep(20000);  // Giả lập thời gian xử lý đơn hàng (ví dụ: 20 giây)
            // Giải phóng khóa Redis sau khi hoàn thành xử lý
            redisTemplate.delete(redisKey);
        }

        VoucherForOrderResponse voucherForOrderResponse = useVoucher(priceOrder, userVoucher, request);

        //---ngoài vòng lặp ...
        //---lưu thông tin shop và voucher vào order
        OrderEntity orderEntity = orderRepository.findById(orderId).orElse(null);
        if (orderEntity == null)
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);

        orderEntity.setMessage(request.getMessage());
        orderEntity.setIdShop(request.getIdShop());
        if (voucherForOrderResponse.getUseVoucher()) {
            orderEntity.setIdUserVoucher(request.getIdUserVoucher());

            userVoucher.setIsValid(false);
            userVoucherRepository.save(userVoucher);
        }
        orderEntity.setLastPrice(voucherForOrderResponse.getLastPrice());
        orderEntity.setDiscountVoucher(voucherForOrderResponse.getDiscountVoucher());
        orderRepository.save(orderEntity);
        return new ApiResponse<>(200, "Lưu đơn hàng shop thành công", result);
    }

    //---lấy ra giá của đơn hàng sau khi dùng voucher
    public VoucherForOrderResponse useVoucher(Double orderPrice, UserVoucher userVoucher, OrderDetailsRequest request) {
        try {
            //lấy gia tổng số tiền của đơn hàng
            Boolean isUserVoucherValid = checkValidVoucher(userVoucher, request);

            Double lastPrice = orderPrice;
            Double discountVoucher;
            Boolean canUseVoucher = true;

            System.out.println("is valid voucher: " + isUserVoucherValid);
            System.out.println("last price: " + lastPrice);
            System.out.println("min accept: " + userVoucher.getVoucher().getMinOrderValue());
            if (isUserVoucherValid == false || lastPrice < userVoucher.getVoucher().getMinOrderValue()) {
                System.out.println("Khong dung voucher");
                return VoucherForOrderResponse.builder()
                        .lastPrice(orderPrice)
                        .discountVoucher(0.0)
                        .useVoucher(false)
                        .build();
            }

            //---giảm theo giá tiền
            if (userVoucher.getVoucher().getDiscountType().name().equalsIgnoreCase(EnumDiscountType.FixedAmount.name())) {
                lastPrice = lastPrice - userVoucher.getVoucher().getDiscountValue();
            }
            //---giảm theo phần trăm
            else if (userVoucher.getVoucher().getDiscountType().name().equalsIgnoreCase(EnumDiscountType.Percentage.name())) {
                lastPrice = lastPrice - lastPrice * (userVoucher.getVoucher().getDiscountValue()) / 100.0;
            }

            System.out.println("last price: " + lastPrice);

            return VoucherForOrderResponse.builder()
                    .lastPrice(Math.max(0, lastPrice))
                    .discountVoucher(orderPrice - lastPrice)
                    .useVoucher(true)
                    .build();
        } catch (Exception e) {
            System.out.println("Loi xư ly voucher(exception): " + e);
            return VoucherForOrderResponse.builder()
                    .lastPrice(orderPrice)
                    .discountVoucher(0.0)
                    .useVoucher(false)
                    .build();
        }
    }

    public Boolean checkValidVoucher(UserVoucher userVoucher, OrderDetailsRequest request) {
        try {
            Boolean checkUserVoucher = true;
            //---1. Kiểm tra tồn tại
            if (request.getIdUserVoucher() == null) {
                System.out.println("don hang khong co id user voucher");
                return false;
            }
//            UserVoucher userVoucher = userVoucherRepository.findById(request.getIdUserVoucher()).orElse(null);
            if (userVoucher == null) {
                System.out.println("Khong tim thay user voucher");
                return false;
            }

            //---2.Kiểm tra khả dụng
            if (userVoucher.getIsValid() == false) {
                System.out.println("user voucher invalid");
                return false;
            }
            if (userVoucher.getVoucher().getIsActive() == false) {
                System.out.println("voucher khong active");
                return false;
            }

            LocalDateTime nowTime = UtilsFunction.getVietNameTimeNow();
            if (userVoucher.getVoucher().getStartDate().isAfter(nowTime)) {
                System.out.println("voucher chua bat dau");
                return false;
            }
            if (userVoucher.getVoucher().getEndDate().isBefore(nowTime)) {
                System.out.println("voucher het han");
                return false;
            }

            //---3.Kiểm tra xem co phải của shop không
            Shop shop = shopRepository.findById(request.getIdShop()).orElse(null);
            if (shop == null) {
                System.out.println("Khong tim thay shop");
                return false;
            }

            System.out.println("id shop: " + shop.getId());
            System.out.println("id shop voucher: " + userVoucher.getVoucher().getShop().getId());
            if (shop.getId() != userVoucher.getVoucher().getShop().getId()) {
                System.out.println("Shop mua hang khong khop voi shop cua voucher");
                return false;
            }

            return true;
        } catch (Exception e) {
            System.out.println("Loi kiem tra voucher hop le(order service): " + e);
            return false;
        }
    }

    @Override
    public ApiResponse<List<OrderKeyDTO>> buyMany(OrderRequest request) throws InterruptedException {
        //----duyệt qua mỗi shop (mỗi shop tạo 1 đơn hàng)

        //---duyệt qua mỗi shop

        List<OrderKeyDTO> result = new ArrayList<>();

        for (var it : request.getOrderDetails()) {
            var orderCreateAPI = addOneOrder(request);
            Long idOrder = orderCreateAPI.getData();

            var addOrderAPI = addOneOrderDetails(it, idOrder);
            result.add(addOrderAPI.getData());
        }

//        System.out.println("ending...");
        //---end of duyệt qua mỗi shop
        return new ApiResponse<>(200, "Mua hàng thành công", result);

    }

    //    ClientOrderController
    //----hủy đơn hàng
    @Override
    public ApiResponse<String> userCancelledOrder(OrderKeyRequest request) {
        OrderEntity orderEntity = orderRepository.findById(request.getIdOrder()).orElse(null);
        if (orderEntity == null) {
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }

        // Lấy thông tin tài khoản hiện tại
        Account account = accountService.getMyAccount();

        // Kiểm tra nếu đơn hàng không thuộc về tài khoản hiện tại
        if (!account.getId().equals(orderEntity.getAccount().getId())) {
            throw new AppException(ErrorCode.ORDER_IS_NOT_U);
        }

        // Kiểm tra trạng thái đơn hàng (chỉ được hủy nếu trạng thái là PENDING)
        if (!orderEntity.getStatusOrder().equals(StatusOrderEnum.PENDING)) {
//            return new ApiResponse<>(400, "Chir huyr", orderEntity.getStatusOrder().name());
            throw new AppException(ErrorCode.CAN_CANCEL_PENDING);
        }

        // Cập nhật trạng thái đơn hàng thành CANCELED
        orderEntity.setStatusOrder(StatusOrderEnum.CANCELED);
        OrderEntity orderSave = orderRepository.save(orderEntity);
        if (orderSave == null || orderSave.getId() == 0) {
            throw new AppException(ErrorCode.CANNOT_SAVE_ORDER);
        }

        // Ghi nhận lịch sử tracking đơn hàng
        TrackingEntity trackingEntity = new TrackingEntity(
                null,
                StatusOrderEnum.CANCELED,
                request.getNote(),
                TypeUserEnum.USER,
                orderSave
        );
        TrackingEntity trackingSave = trackingRepository.save(trackingEntity);
        if (trackingSave == null || trackingSave.getId() == 0) {
            throw new AppException(ErrorCode.CANNOT_SAVE_TRACKING);
        }

        // Tạo nội dung thông báo
        String actorName = account.getUsername(); // Người thực hiện hủy đơn hàng
        String notificationContent = "Người dùng " + actorName + " đã hủy đơn hàng.";

        if (account.getId().equals(orderEntity.getIdShop())) {
            // Nếu shop hủy đơn hàng
            notificationContent = "Shop đã hủy đơn hàng của bạn.";
        }


        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setAccId(orderEntity.getIdShop());
        notificationRequest.setActorId(account.getId());
        notificationRequest.setUsername("null");
        notificationRequest.setActorName(actorName);
        notificationRequest.setType(EnumTypeStatus.canceled);
        notificationRequest.setContent(notificationContent);
        notificationRequest.setStatus(EnumNotificationStatus.unseen);

        Notification notification = notificationMapper.toEntity(notificationRequest);
        notificationRepository.save(notification);

        // Gửi thông báo
        if (account.getId().equals(orderEntity.getIdShop())) {
            // Shop hủy đơn hàng => Thông báo cho người dùng
            notificationService.senNotification(orderEntity.getAccount().getId().toString(), notification);
        } else {
            // Người dùng hủy đơn hàng => Thông báo cho Shop
            notificationService.senNotification(orderEntity.getIdShop().toString(), notification);
        }

        // Cập nhật stock
        updateStock(orderEntity);

        return new ApiResponse<>(200, "Hủy đơn hàng thành công", null);
    }


    @Override
    public ApiResponse<String> userChangeStatusOrder(OrderKeyRequest request, StatusOrderEnum status) {
        // Kiểm tra đơn hàng có tồn tại không
        OrderEntity order = orderRepository.findById(request.getIdOrder()).orElse(null);
        if (order == null) {
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }

        // Kiểm tra đơn hàng có phải của user hiện tại không
        Account account = accountService.getMyAccount();
        if (!account.getId().equals(order.getAccount().getId())) {
            throw new AppException(ErrorCode.ORDER_IS_NOT_U);
        }

        // Tạo tracking cho đơn hàng
        TrackingEntity trackingEntity = new TrackingEntity(null, null, null, TypeUserEnum.USER, order);

        // a. Trạng thái RECEIVED
        if (status.equals(StatusOrderEnum.RECEIVED)) {
            if (!order.getStatusOrder().equals(StatusOrderEnum.ACCEPT)) {
                throw new AppException(ErrorCode.ORDER_NOT_ACCPET_LAST);
            }
            order.setStatusOrder(StatusOrderEnum.RECEIVED);
            order.setStatusPayment(StatusPaymentEnum.PAID);
            trackingEntity.setStatus(StatusOrderEnum.RECEIVED);
            trackingEntity.setNote(request.getNote());
        }

        // b. Trạng thái RETURNED
        if (status.equals(StatusOrderEnum.RETURNED)) {
            if (!order.getStatusOrder().equals(StatusOrderEnum.RECEIVED)) {
                throw new AppException(ErrorCode.ORDER_NOT_RECEIVED_LAST);
            }
            order.setStatusOrder(StatusOrderEnum.RETURNED);
            order.setStatusPayment(StatusPaymentEnum.PENDING_REFUND);
            trackingEntity.setStatus(StatusOrderEnum.RETURNED);
            trackingEntity.setNote(request.getNote());
        }

        // c. Trạng thái NOT_RECEIVED
        if (status.equals(StatusOrderEnum.NOT_RECEIVED)) {
            if (!order.getStatusOrder().equals(StatusOrderEnum.ACCEPT)) {
                throw new AppException(ErrorCode.ORDER_NOT_ACCPET_LAST);
            }
            order.setStatusOrder(StatusOrderEnum.NOT_RECEIVED);
            if (order.getStatusPayment().equals(StatusPaymentEnum.PAID)) {
                order.setStatusPayment(StatusPaymentEnum.PENDING_REFUND);
            }
            trackingEntity.setStatus(StatusOrderEnum.NOT_RECEIVED);
            trackingEntity.setNote(request.getNote());
        }

        // Lưu cập nhật đơn hàng và tracking
        OrderEntity orderSave = orderRepository.save(order);
        TrackingEntity trackingSave = trackingRepository.save(trackingEntity);
        if (orderSave == null || trackingSave == null || orderSave.getId() == null || trackingSave.getId() == null) {
            throw new AppException(ErrorCode.CANNOT_SAVE_ORDER);
        }
        String actorName = account.getUsername();
        String notificationContent = switch (status) {
            case RECEIVED -> "Người dùng " + actorName + " đã xác nhận đã nhận hàng.";
            case RETURNED -> "Người dùng " + actorName + " đã yêu cầu trả hàng.";
            case NOT_RECEIVED -> "Người dùng " + actorName + " đã xác nhận không nhận được hàng.";
            default -> throw new IllegalStateException("Trạng thái không hợp lệ: " + status);
        };
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setAccId(order.getIdShop());
        notificationRequest.setActorId(account.getId());
        notificationRequest.setUsername("null");
        notificationRequest.setActorName(actorName);
        notificationRequest.setType(EnumTypeStatus.order_status_changed);
        notificationRequest.setContent(notificationContent);
        notificationRequest.setStatus(EnumNotificationStatus.unseen);

        Notification notification = notificationMapper.toEntity(notificationRequest);
        notificationRepository.save(notification);
        notificationService.senNotification(order.getIdShop().toString(), notification);
        return new ApiResponse<>(200, "Cập nhật đơn hàng thành công", null);
    }


    @Override
    public ApiResponse<String> adminAcceptStatusOrder(OrderKeyRequest request) {
        // Kiểm tra đơn hàng có tồn tại không
        OrderEntity order = orderRepository.findById(request.getIdOrder()).orElse(null);
        if (order == null) {
            throw new AppException(ErrorCode.ORDER_NOT_FOUND);
        }

        // Kiểm tra tài khoản admin hiện tại
        Account account = accountService.getMyAccount();
        if (account == null) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }

        // Kiểm tra shop có tồn tại và có thuộc về tài khoản hiện tại không
        Shop shop = shopRepository.findByEmail(account.getEmail());
        if (shop == null) {
            throw new AppException(ErrorCode.SHOP_CAN_NOT_FOUND);
        }
        if (!shop.getId().equals(order.getIdShop())) {
            throw new AppException(ErrorCode.ORDER_IS_NOT_YOUR_SHOP);
        }

        // Kiểm tra trạng thái đơn hàng (chỉ được chấp nhận nếu trạng thái là PENDING)
        if (!order.getStatusOrder().equals(StatusOrderEnum.PENDING)) {
            throw new AppException(ErrorCode.ORDER_NOT_PENDING_LAST);
        }

        //---nếu thanh toán vnpay nhưng chưa pay cũng không thanh toán được
        if(order.getTypePayment().name().equalsIgnoreCase(TypePaymenEnum.VNPAY.name())){
            if(order.getStatusPayment().name().equalsIgnoreCase(StatusPaymentEnum.UNPAID.name()))
                throw new AppException(ErrorCode.ORDER_VN_PAY_NOT_PAID);
        }

        // Cập nhật trạng thái đơn hàng thành ACCEPT
        order.setStatusOrder(StatusOrderEnum.ACCEPT);

        // Ghi nhận lịch sử tracking đơn hàng
        TrackingEntity trackingEntity = new TrackingEntity(
                null,
                StatusOrderEnum.ACCEPT,
                request.getNote(),
                TypeUserEnum.SHOP,
                order
        );

        // Lưu cập nhật đơn hàng và tracking
        OrderEntity orderSave = orderRepository.save(order);
        TrackingEntity trackingSave = trackingRepository.save(trackingEntity);
        if (orderSave == null || trackingSave == null || orderSave.getId() == null || trackingSave.getId() == null) {
            throw new AppException(ErrorCode.CANNOT_SAVE_ORDER);
        }
        String shopName = shop.getShopName();
        String notificationContent = "Shop " + shopName + " đã xác nhận đơn hàng của bạn.";
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setAccId(order.getAccount().getId());
        notificationRequest.setActorId(shop.getId());
        notificationRequest.setUsername(order.getAccount().getUsername());
        notificationRequest.setActorName(shopName);
        notificationRequest.setType(EnumTypeStatus.order_status_changed);
        notificationRequest.setContent(notificationContent);
        notificationRequest.setStatus(EnumNotificationStatus.unseen);
        Notification notification = notificationMapper.toEntity(notificationRequest);
        notificationRepository.save(notification);
        notificationService.senNotification(order.getAccount().getId().toString(), notification);

        return new ApiResponse<>(200, "Cập nhật đơn hàng thành công", null);
    }


    @Override
    public List<OrderEntity> getListOrderByStatus(Long idShop, StatusOrderEnum status, LocalDate start, LocalDate end) {
        try {
            return orderRepository.listOrderByStatus(idShop, status, start, end);
        } catch (Exception e) {
            System.out.println("Loi khong the lay list order accept(order service): " + e);
            return null;
        }
    }

    @Override
    public ApiResponse<StaticOrderResponse> staticOrderMyShop(LocalDate startDate, LocalDate endDate) {
        Account account = accountService.getMyAccount();
        if (account == null)
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);

        Shop shop = shopRepository.findByEmail(account.getEmail());
        if (shop == null)
            throw new AppException(ErrorCode.SHOP_CAN_NOT_FOUND);

        StaticOrderResponse result = new StaticOrderResponse();

        //----pending
        List<OrderEntity> getAllPending = getListOrderByStatus(shop.getId(), StatusOrderEnum.PENDING, startDate, endDate);
        if (getAllPending == null) {
//            System.out.println("Pending is null");
            result.setCountPending(null);
        } else {
//            System.out.println("Pending not null");
            result.setCountPending(Long.valueOf(getAllPending.size()));
        }

        //----all accept
        List<OrderEntity> getAllAcept = getListOrderByStatus(shop.getId(), StatusOrderEnum.ACCEPT, startDate, endDate);
        if (getAllAcept == null)
            result.setCountAccept(null);
        else
            result.setCountAccept(Long.valueOf(getAllAcept.size()));

        //----all cancel
        var getAllCancel = getListOrderByStatus(shop.getId(), StatusOrderEnum.CANCELED, startDate, endDate);
        if (getAllCancel == null)
            result.setCountCancelled(null);
        else
            result.setCountCancelled(Long.valueOf(getAllCancel.size()));

        //----all returnded(tra hang)
        var getAllReturned = getListOrderByStatus(shop.getId(), StatusOrderEnum.RETURNED, startDate, endDate);
        if (getAllReturned == null)
            result.setCountRefund(null);
        else
            result.setCountRefund(Long.valueOf(getAllReturned.size()));

        //----san pham bi khoa
//        IProductRepository
        var getAllReport = productService.getAllReport(shop.getId(), startDate, endDate);
        if (getAllReport == null)
            result.setCountReport(null);
        else
            result.setCountReport(Long.valueOf(getAllReport.size()));

        //-----san pham het hang
        var getAllProductOutOfStock = productService.getAllOutStock(shop.getId());
        if (getAllProductOutOfStock == null)
            result.setNoStock(null);
        else
            result.setNoStock(Long.valueOf(getAllProductOutOfStock.size()));


        return new ApiResponse<>(200, "Thống kê cửa hàng", result);
    }

    @Override
    public ApiResponse<String> payVNPaySuccess(OrderIdsRequest requests) {
        for (var it : requests.getIdOrder()) {
            OrderEntity orderEntity = orderRepository.findById(it).orElse(null);
            if (orderEntity == null)
                throw new AppException(ErrorCode.ORDER_NOT_FOUND);

            orderEntity.setStatusPayment(StatusPaymentEnum.PAID);
            orderRepository.save(orderEntity);
        }
        return null;
    }

    @Override
    public ApiResponse<Page<OrderOfShopResponse>> getMyOrderShop(SearchOrder searchOrder, Pageable pageable) {
        Account account = accountService.getMyAccount();
        if (account == null)
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);

        Shop shop = shopRepository.findByEmail(account.getEmail());

        if (shop == null) {
            throw new AppException(ErrorCode.SHOP_NOT_FOUND);
        }

        //----lấy id shop
        searchOrder.setIdShop(shop.getId());
        Specification<OrderEntity> orderSpe = Specification
                .where(OrderShopSpecification.hasIdShop(searchOrder.getIdShop()))
                .and(OrderShopSpecification.hasKeySearch(searchOrder.getKeySearch()))
                .and(OrderShopSpecification.hasOrderStatus(searchOrder.getOrderStatus()))
                .and(OrderShopSpecification.hasStatusPayment(searchOrder.getTypePayment()))
                .and(OrderShopSpecification.hasStartDate(searchOrder.getStartDate()))
                .and(OrderShopSpecification.hasEndDate(searchOrder.getEndDate()));
        ;

//        System.out.println("start date: " + searchOrder.getStartDate());
//        System.out.println("end date: " + searchOrder.getEndDate());

        Page<OrderEntity> order = orderRepository.findAll(orderSpe, pageable);

        return new ApiResponse<>(200, "Danh sách đơn hàng của shop",
                order.map(it -> orderOfShopMapper.convertToOrderShopResponse(it)));
    }

    @Override
    public void updateStock(OrderEntity entity) {
        List<OrderDetailEntity> entities = new ArrayList<>();
        if (entity != null)
            entities = entity.getOrderDetail();

//        System.out.println("So luong entities: " + entities.size());
        for (var it : entities) {

            StockInfo stockInfo = stockService.getStockInfo(variantMapper.convertStringToDTO(it.getVariant()), it.getProduct().getId());

//            System.out.println("Stock inffor: " + stockInfo);
            if (stockInfo != null) {
//                System.out.println("Yebbbb");
                StockEntity stock = stockRepository.findById(stockInfo.getId()).orElse(null);
                if (stock != null) {
                    stock.setQuantity(it.getQuantity() + stock.getQuantity());
                    stockRepository.save(stock);
                }
            }
        }
    }

//ProductService

    @Override
    public ApiResponse<List<OrderShopResponse>> getListOrderByUser(String productName, Pageable pageable) {
        String email = jwtProvider.getEmailContext();
        if (email == null || email.isEmpty()) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
        List<OrderEntity> orders = orderRepository.findByAccountIdAndProductName(account.getId(), productName, pageable);

        if (orders.isEmpty()) {
            return ApiResponse.<List<OrderShopResponse>>builder()
                    .message("No orders found for the user.")
                    .data(List.of())
                    .build();
        }
        List<OrderShopResponse> orderShopResponses = orders.stream()
                .map(order -> {
                    OrderShopResponse response = orderMapper.toResponse(order);

                    // Kiểm tra nếu có phản hồi (feedback) liên quan đến order details
                    boolean isFeedback = order.getOrderDetail().stream()
                            .anyMatch(detail -> detail.getFeedBack() != null && !detail.getFeedBack().isEmpty());

                    response.setIsFeedback(isFeedback);
                    return response;
                })
                .toList();

        return ApiResponse.<List<OrderShopResponse>>builder()
                .message("List of orders fetched successfully.")
                .data(orderShopResponses)
                .build();
    }


    @Override
    public ApiResponse<List<OrderShopResponse>> getListOrderByUser(String productName, StatusOrderEnum status, Pageable pageable) {
        String email = jwtProvider.getEmailContext();
        if (email == null || email.isEmpty()) {
            throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

        List<OrderEntity> orders = orderRepository.findByAccountIdProductNameAndStatus(account.getId(), productName, status, pageable);

        if (orders.isEmpty()) {
            return ApiResponse.<List<OrderShopResponse>>builder()
                    .message("No orders found for the user.")
                    .data(List.of())
                    .build();
        }

        List<OrderShopResponse> orderShopResponses = orders.stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());

        return ApiResponse.<List<OrderShopResponse>>builder()
                .message("List of orders fetched successfully.")
                .data(orderShopResponses)
                .build();
    }

    @Override
    public ApiResponse<OrderShopResponse> getOrderByUser(Long id) {
        try {
            String email = jwtProvider.getEmailContext();
            if (email == null || email.isEmpty()) {
                throw new AppException(ErrorCode.ACCOUNT_NOT_FOUND);
            }
            Account account = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
            OrderEntity order = orderRepository.findByIdAndAccountId(id, account.getId())
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
            OrderShopResponse orderResponse = orderMapper.toResponse(order);
            return ApiResponse.<OrderShopResponse>builder()
                    .message("Order fetched successfully.")
                    .data(orderResponse)
                    .build();
        } catch (AppException e) {
            return ApiResponse.<OrderShopResponse>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<OrderShopResponse>builder()
                    .code(500)
                    .message("An unexpected error occurred: " + e.getMessage())
                    .build();
        }
    }


    @Override
    public ApiResponse<List<ShopRevenueDTO>> getTop5Shops(LocalDate startDate, LocalDate endDate) {
        return null;
    }

    Boolean checkPrdIsMyShop(ProductEntity entity) {
        try {
            Account account = accountService.getMyAccount();
            Shop shop = shopRepository.findByEmail(account.getEmail());
            if (shop.getId() == entity.getShop().getId()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println("Khong the check duoc san pham có phai cua shop khong (order service): " + e);
            return false;
        }
    }
}

//ClientChattingController
//StaticsShopController
//ClientProductController

//UserRole