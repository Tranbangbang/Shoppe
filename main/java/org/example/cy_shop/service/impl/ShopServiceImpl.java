package org.example.cy_shop.service.impl;

import org.example.cy_shop.configuration.jwtConfig.JwtProvider;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.address.AddressRequest;
import org.example.cy_shop.dto.request.shop.ShopRequest;
import org.example.cy_shop.dto.response.auth.AccountResponse;
import org.example.cy_shop.dto.response.product.ProductShopStatisticsResponse;
import org.example.cy_shop.dto.response.shop.ShopDetailResponse;
import org.example.cy_shop.dto.response.shop.ShopResponse;
import org.example.cy_shop.dto.response.shop.ShopRevenueResponse;
import org.example.cy_shop.entity.*;
import org.example.cy_shop.enums.order.StatusOrderEnum;
import org.example.cy_shop.enums.order.StatusPaymentEnum;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.AccountMapper;
import org.example.cy_shop.mapper.AddressMapper;
import org.example.cy_shop.mapper.ShopMapper;
import org.example.cy_shop.repository.IAccountRepository;
import org.example.cy_shop.repository.IRoleRepository;
import org.example.cy_shop.repository.IShopRepository;
import org.example.cy_shop.repository.order.IOrderDetailRepository;
import org.example.cy_shop.repository.order.IOrderRepository;
import org.example.cy_shop.service.IShopService;
import org.example.cy_shop.utils.Const;
import org.example.cy_shop.utils.UtilsFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShopServiceImpl implements IShopService {
    @Autowired
    private IShopRepository shopRepository;
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private IAccountRepository accountRepository;
    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    IRoleRepository roleRepository;
    @Autowired
    IOrderDetailRepository orderDetailRepository;
    @Autowired
    IOrderRepository orderRepository;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private StringHttpMessageConverter stringHttpMessageConverter;

    @Override
    public ShopResponse findByEmail(String email) {
        try {
            var shopEntity = shopRepository.findByEmail(email);
            return shopMapper.toResponse(shopEntity);
        }catch (Exception e){
            System.out.println("Khong the tim shop theo email (shop service): " + e);
            return null;
        }
    }

    @Override
    public ShopResponse findByUserName(String userName) {
        try {
            var shop =  shopRepository.findByUserName(userName).orElse(null);
            if(shop == null)
                return null;
            return shopMapper.toResponse((Shop) shop);
        }catch (Exception e){
            System.out.println("Lỗi không tìm thấy shop (shop service): " + e);
            return null;
        }
    }

    @Override
    public ApiResponse<ShopResponse> createShop(ShopRequest shopRequest) {
        try {
            String email = jwtProvider.getEmailContext();
            if (email == null || email.isEmpty()) {
                throw new AppException(ErrorCode.USER_NOT_FOUND);
            }
            Account account = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
            /*
            if (shopRequest.getShopName().matches(".*[^\\p{L}\\d\\s].*")) {
                throw new AppException(ErrorCode.INVALID_SHOP_NAME);
            }
             */
            if (shopRepository.existsByShopName(shopRequest.getShopName())) {
                throw new AppException(ErrorCode.SHOP_NAME_ALREADY_EXISTS);
            }
            AddressRequest addressRequest = shopRequest.getAddress();
            if (addressRequest.getPhoneNumber().startsWith("-") || !addressRequest.getPhoneNumber().matches("\\d+")) {
                throw new AppException(ErrorCode.INVALID_PHONE_NUMBER);
            }
            Address address = addressMapper.toEntity(shopRequest.getAddress());
            Shop shop = shopMapper.toEntity(shopRequest);
            shop.setAccount(account);

            // Sử dụng thời gian Việt Nam
            LocalDateTime vietNamTimeNow = UtilsFunction.getVietNameTimeNow();
            shop.setCreatedAt(vietNamTimeNow);
            shop.setUpdatedAt(vietNamTimeNow);
            shop.setIsApproved(false);
            shop.setDetailedAddress(address);
            Shop savedShop = shopRepository.save(shop);
            Role shopRole = roleRepository.findByName(Const.ROLE_SHOP);
            if (shopRole == null) {
                throw new AppException(ErrorCode.ROLE_NOT_FOUND);
            }
            UserRole accountUserRole = new UserRole();
            accountUserRole.setAccount(account);
            accountUserRole.setRole(shopRole);
            account.setUserRoles(Set.of(accountUserRole));
            accountRepository.save(account);

            ShopResponse shopResponse = shopMapper.toResponse(savedShop);
            return ApiResponse.<ShopResponse>builder()
                    .data(shopResponse)
                    .message("Shop created successfully")
                    .build();
        } catch (AppException e) {
            return ApiResponse.<ShopResponse>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<ShopResponse>builder()
                    .code(101)
                    .message("Failed : " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<Map<String, Object>> listShop(Pageable pageable, String shopName) {
        try {
            Page<Shop> shopPage;
            if (shopName != null && !shopName.trim().isEmpty()) {
                shopPage = shopRepository.findByShopNameContainingIgnoreCase(shopName, pageable);
            } else {
                shopPage = shopRepository.findAll(pageable);
            }
            List<Shop> shopList = shopPage.getContent();
            List<ShopDetailResponse> shopDetailResponses = shopMapper.toResponseDetail(shopList);

            Map<String, Object> response = new HashMap<>();
            response.put("shops", shopDetailResponses);
            response.put("totalElements", shopPage.getTotalElements());
            response.put("totalPages", shopPage.getTotalPages());
            response.put("currentPage", pageable.getPageNumber());
            response.put("pageSize", pageable.getPageSize());

            return ApiResponse.<Map<String, Object>>builder()
                    .data(response)
                    .message("List of shops retrieved successfully")
                    .build();
        } catch (Exception e) {
            // Xử lý lỗi
            return ApiResponse.<Map<String, Object>>builder()
                    .code(500)
                    .message("Failed to retrieve shops: " + e.getMessage())
                    .build();
        }
    }



    /*
    @Override
    public ApiResponse<List<ShopDetailResponse>> listShop(Pageable pageable) {
        Page<Shop> shopPage = shopRepository.findAll(pageable);
        List<Shop> shopList = shopPage.getContent();
        List<ShopDetailResponse> shopDetailResponses = shopMapper.toResponseDetail(shopList);

        return ApiResponse.<List<ShopDetailResponse>>builder()
                .data(shopDetailResponses)
                .message("List of shops retrieved successfully")
                .build();
    }


     */

    @Override
    public ApiResponse<Shop> findMyShop() {
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        String jwt = authenticationFilter.getJwtFromRequest(request);
//        boolean isLogin = authenticationFilter.validateToken(JWT_SECRET_ACCESS_TOKEN, jwt);
//        if(isLogin == false)
//            return new ApiResponse<>(ErrorCode.USER_NOT_LOGIN.getCode(), ErrorCode.USER_NOT_LOGIN.getMessage(), null);

        String email = null;
        try {
            email = jwtProvider.getEmailContext();
        }catch (Exception e){
            return new ApiResponse<>(ErrorCode.USER_NOT_LOGIN.getCode(), ErrorCode.USER_NOT_LOGIN.getMessage(), null);
        }

        Account account = accountRepository.findByEmail(email).orElse(null);

        if(account == null)
            return new ApiResponse<>(ErrorCode.USER_NOT_LOGIN.getCode(), ErrorCode.USER_NOT_LOGIN.getMessage(), null);


        Shop shop = shopRepository.findByEmail(email);
        if(shop == null)
            return new ApiResponse<>(ErrorCode.USER_CAN_NOT_FIND_SHOP.getCode(), ErrorCode.USER_CAN_NOT_FIND_SHOP.getMessage(), null);

        return new ApiResponse<>(200, "Id shop", shop);
    }

    @Override
    public ApiResponse<String> deleteShopByNotActive() {
        try{
            String email = jwtProvider.getEmailContext();
            if (email == null || email.isEmpty()) {
                throw new AppException(ErrorCode.USER_NOT_FOUND);
            }
            Account account = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

            Optional<Shop> shopOptional = shopRepository.findByAccount(account);
            if (shopOptional.isEmpty()) {
                throw new AppException(ErrorCode.SHOP_NOT_FOUND);
            }

            Shop shop = shopOptional.get();
            if (Boolean.TRUE.equals(shop.getIsApproved())) {
                throw new AppException(ErrorCode.SHOP_ALREADY_APPROVED);
            }
            shopRepository.delete(shop);
            Set<UserRole> userRoles = account.getUserRoles();
            userRoles.removeIf(userRole -> userRole.getRole().getName().equals(Const.ROLE_SHOP));
            account.setUserRoles(userRoles);
            accountRepository.save(account);
            return ApiResponse.<String>builder()
                    .message("Shop and related roles deleted successfully")
                    .build();
        } catch (AppException e) {
            return ApiResponse.<String>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .message("Failed : " + e.getMessage())
                    .build();
        }
    }

    @Override
    public List<ProductShopStatisticsResponse> getTopSellingProductsForShop(LocalDateTime startDate, LocalDateTime endDate) {

        String email = jwtProvider.getEmailContext();
        Shop shop = shopRepository.findByEmail(email);
        Long shopId = shop.getId();

        List<Object[]> results = orderDetailRepository.findTopSellingProductsByShop(
                StatusOrderEnum.RECEIVED.name(),
                StatusPaymentEnum.PAID.name(),
                startDate,
                endDate,
                shopId
        );

        return results.stream()
                .map(row -> new ProductShopStatisticsResponse(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        ((Number) row[3]).doubleValue(),
                        ((Number) row[4]).doubleValue(),
                        (String) row[5],
                        ((Number) row[6]).longValue()
                ))
                .limit(5)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductShopStatisticsResponse> getTopSellingProductsToday() {
        String email = jwtProvider.getEmailContext();
        Shop shop = shopRepository.findByEmail(email);
        if (shop == null) {
            throw new AppException(ErrorCode.SHOP_NOT_FOUND);
        }

        Long shopId = shop.getId();
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        List<Object[]> results = orderDetailRepository.findTopSellingProductsByShop(
                StatusOrderEnum.RECEIVED.name(),
                StatusPaymentEnum.PAID.name(),
                startOfDay,
                endOfDay,
                shopId
        );

        return results.stream()
                .map(row -> new ProductShopStatisticsResponse(
                        ((Number) row[0]).longValue(),
                        (String) row[1],
                        (String) row[2],
                        ((Number) row[3]).doubleValue(),
                        ((Number) row[4]).doubleValue(),
                        (String) row[5],
                        ((Number) row[6]).longValue()
                ))
                .limit(5)
                .collect(Collectors.toList());
    }



    @Override
    public ShopRevenueResponse getTodayShopRevenue(LocalDateTime startOfDay, LocalDateTime endOfDay) {
        String email = jwtProvider.getEmailContext();
        Shop shop = shopRepository.findByEmail(email);
        if (shop == null) {
            throw new AppException(ErrorCode.SHOP_NOT_FOUND);
        }
        Long shopId = shop.getId();
        StatusOrderEnum statusOrder = StatusOrderEnum.CANCELED;
        return orderRepository.getShopRevenue(shopId, statusOrder, startOfDay, endOfDay);
    }

    @Override
    public ApiResponse<AccountResponse> getAccountByShopName(String shopName) {
        try {
            Account account = shopRepository.findAccountByShopName(shopName);
            if (account == null) {
                return ApiResponse.<AccountResponse>builder()
                        .code(404)
                        .message("No account found for shop name: " + shopName)
                        .build();
            }
            AccountResponse accountResponse = accountMapper.toResponse(account);
            return ApiResponse.<AccountResponse>builder()
                    .code(200)
                    .data(accountResponse)
                    .message("Account fetched successfully.")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<AccountResponse>builder()
                    .code(500)
                    .message("Error fetching account: " + e.getMessage())
                    .build();
        }
    }
    @Override
    public ApiResponse<ShopDetailResponse> getShopProfile() {
        try {
            String email = jwtProvider.getEmailContext();
            if (email == null || email.isEmpty()) {
                throw new AppException(ErrorCode.USER_NOT_FOUND);
            }
            Shop shop = shopRepository.findByEmail(email);
            if (shop == null) {
                throw new AppException(ErrorCode.SHOP_NOT_FOUND);
            }
            ShopDetailResponse shopDetailResponse = shopMapper.toResponseDetail1(shop);

            return ApiResponse.<ShopDetailResponse>builder()
                    .data(shopDetailResponse)
                    .message("Shop profile fetched successfully")
                    .build();
        } catch (AppException e) {
            return ApiResponse.<ShopDetailResponse>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<ShopDetailResponse>builder()
                    .code(101)
                    .message("Failed to fetch shop profile: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<ShopDetailResponse> updateShopProfile(ShopRequest shopRequest) {
        try {
            String email = jwtProvider.getEmailContext();
            if (email == null || email.isEmpty()) {
                throw new AppException(ErrorCode.USER_NOT_FOUND);
            }
            Shop shop = shopRepository.findByEmail(email);
            if (shop == null) {
                throw new AppException(ErrorCode.SHOP_NOT_FOUND);
            }
            if (shopRequest.getShopName() != null) {
                if (shopRepository.existsByShopName(shopRequest.getShopName()) &&
                        !shop.getShopName().equals(shopRequest.getShopName())) {
                    throw new AppException(ErrorCode.SHOP_NAME_ALREADY_EXISTS);
                }
                shop.setShopName(shopRequest.getShopName());
            }
            if (shopRequest.getAddress() != null) {
                Address updatedAddress = addressMapper.toEntity(shopRequest.getAddress());
                shop.setDetailedAddress(updatedAddress);
            }
            shop.setUpdatedAt(UtilsFunction.getVietNameTimeNow());
            Shop updatedShop = shopRepository.save(shop);
            ShopDetailResponse shopDetailResponse = shopMapper.toResponseDetail1(updatedShop);
            return ApiResponse.<ShopDetailResponse>builder()
                    .data(shopDetailResponse)
                    .message("Shop profile updated successfully")
                    .build();
        } catch (AppException e) {
            return ApiResponse.<ShopDetailResponse>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<ShopDetailResponse>builder()
                    .code(101)
                    .message("Failed to update shop profile: " + e.getMessage())
                    .build();
        }
    }



}
