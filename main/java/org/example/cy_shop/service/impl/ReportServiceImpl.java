package org.example.cy_shop.service.impl;

import org.example.cy_shop.configuration.jwtConfig.JwtProvider;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.notification.NotificationRequest;
import org.example.cy_shop.dto.request.report.ReportRequest;
import org.example.cy_shop.dto.response.report.ReportResponse;
import org.example.cy_shop.dto.response.shop.ShopResponse;
import org.example.cy_shop.dto.response.product.ProductResponse;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.Notification;
import org.example.cy_shop.entity.Report;
import org.example.cy_shop.entity.Shop;
import org.example.cy_shop.entity.order.OrderEntity;
import org.example.cy_shop.entity.product.ProductEntity;
import org.example.cy_shop.enums.EnumNotificationStatus;
import org.example.cy_shop.enums.EnumReportStatus;
import org.example.cy_shop.enums.EnumTypeStatus;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.mapper.NotificationMapper;
import org.example.cy_shop.mapper.ReportMapper;
import org.example.cy_shop.mapper.ShopMapper;
import org.example.cy_shop.mapper.product_mapper.ProductMapper;
import org.example.cy_shop.repository.*;
import org.example.cy_shop.repository.order.IOrderRepository;
import org.example.cy_shop.repository.product_repository.IProductRepository;
import org.example.cy_shop.service.IEmailService;
import org.example.cy_shop.service.IReportService;
import org.example.cy_shop.service.notification.NotificationService;
import org.example.cy_shop.service.product.IProductService;
import org.example.cy_shop.utils.UtilsFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.example.cy_shop.enums.EnumReportStatus.RESOLVED;

@Service
public class ReportServiceImpl implements IReportService {
    @Autowired
    private IReportRepository reportRepository;
    @Autowired
    private ReportMapper reportMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private IAccountRepository accountRepository;
    @Autowired
    private IProductRepository productRepository;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private IUserRoleRepository userRoleRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private INotificationRepository notificationRepository;
    @Autowired
    private IShopRepository shopRepository;
    @Autowired
    private IProductService productService;
    @Autowired
    private ShopMapper shopMapper;
    @Autowired
    private IEmailService emailService;
    @Autowired
    IOrderRepository orderRepository;


    @Autowired
    private NotificationMapper notificationMapper;
    @Override
    public ApiResponse<String> createReport(ReportRequest reportRequest) {
        try {
            String email = jwtProvider.getEmailContext();
            if (email == null || email.isEmpty()) {
                throw new AppException(ErrorCode.USER_NOT_FOUND);
            }
            Account reporter = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
            Optional<ProductEntity> product = productRepository.findById(reportRequest.getProduct_id());
            if (product.isEmpty()) {
                throw new AppException(ErrorCode.Invalid_product_or_user);
            }
            Optional<Report> existingReport = reportRepository.findByProductAndReporter(product.get(), reporter);
            if (existingReport.isPresent()) {
                throw new AppException(ErrorCode.DUPLICATE_REPORT);
            }
            Report report = reportMapper.toEntity(reportRequest, reporter, product.orElse(null));
            report.setCreatedAt(UtilsFunction.getVietNameTimeNow());
            reportRepository.save(report);
            List<Long> adminAccountIds = userRoleRepository.findAdminAccountIds();
            for (Long adminAccountId : adminAccountIds) {
                Account adminAccount = accountRepository.findById(adminAccountId)
                        .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));

                NotificationRequest notificationRequest = new NotificationRequest();
                notificationRequest.setAccId(adminAccountId);
                notificationRequest.setActorId(reporter.getId());
                notificationRequest.setUsername(adminAccount.getUsername());
                notificationRequest.setActorName(reporter.getUsername());
                notificationRequest.setType(EnumTypeStatus.denounce);
                notificationRequest.setContent(reporter.getUsername() + " đã báo cáo sản phẩm: "
                        + product.get().getProductName() + " của shop: " + product.get().getShop().getShopName() + ".");
                notificationRequest.setStatus(EnumNotificationStatus.unseen);
                Notification notification = notificationMapper.toEntity(notificationRequest);
                notificationRepository.save(notification);
                notificationService.senNotification(adminAccountId.toString(), notification);
            }
            return ApiResponse.<String>builder()
                    .message("Report created successfully")
                    .build();

        } catch (AppException e) {
            return ApiResponse.<String>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .code(101)
                    .message("Failed to create report: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<String> updateReportStatus(Long reportId, EnumReportStatus newStatus) {
        try {
            Report existingReport = reportRepository.findById(reportId)
                    .orElseThrow(() -> new AppException(ErrorCode.REPORT_NOT_FOUND));
            ProductEntity product = existingReport.getProduct();
            if (product == null) {
                throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
            }
            existingReport.setStatus(newStatus);
            reportRepository.save(existingReport);
            Shop shop = product.getShop();
            if (shop == null) {
                throw new AppException(ErrorCode.SHOP_NOT_FOUND);
            }
            if (newStatus == EnumReportStatus.RESOLVED) {
                product.setIsBan(true);
                productRepository.save(product);

                System.out.println("update kho hàng nhé");
                productService.updateCart(product.getId(), false, true);
                productService.updateOrder(product.getId(), false, true);

                //---cập nhật số lượng

//                try {
//
//                    List<OrderEntity> orderList = orderRepository.findbyIdProduct();
//                }catch (Exception e){
//                    System.out.println("Loi update so luong khi report");
//                }
            }

            // Tính số lượng sản phẩm đã bị cấm của shop
            long resolvedProductCount = reportRepository.countByProduct_Shop_IdAndStatus(shop.getId(), EnumReportStatus.RESOLVED);

            // Cập nhật trạng thái shop dựa trên số lượng sản phẩm bị cấm
            boolean shouldApprove = resolvedProductCount < 3;
            if (shop.getIsApproved() != shouldApprove) {
                shop.setIsApproved(shouldApprove);
                shopRepository.save(shop);
                productService.updateOrderShop(shop.getId());
                productService.updateCartShop(shop.getId());
                if (!shouldApprove) {
                    List<ProductResponse> violatedProducts = getViolatedProductsByShop(shop.getId(), Pageable.unpaged()).getData();
                    sendBanEmailToShop(shop, violatedProducts);
                }
            }
            return ApiResponse.<String>builder()
                    .message("Report status updated successfully")
                    .build();
        } catch (AppException e) {
            return ApiResponse.<String>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<String>builder()
                    .code(101)
                    .message("Failed to update report status: " + e.getMessage())
                    .build();
        }
    }
    private void sendBanEmailToShop(Shop shop, List<ProductResponse> violatedProducts) {
        StringBuilder productListHtml = new StringBuilder();
        for (ProductResponse product : violatedProducts) {
            productListHtml.append("<li style=\"margin-bottom: 15px; display: flex; align-items: center;\">")
                    .append("<img src=\"").append(product.getCoverImage()).append("\" alt=\"").append(product.getProductName()).append("\" style=\"width: 50px; height: 50px; object-fit: cover; margin-right: 10px; border-radius: 5px;\">")
                    .append("<div>")
                    .append("<strong>").append(product.getProductName()).append("</strong><br>")
                    .append("<span>").append(product.getProductDescription()).append("</span>")
                    .append("</div>")
                    .append("</li>");
        }


        String emailContent = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "  <meta charset=\"UTF-8\">\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "  <title>Shop Banned Notification</title>\n" +
                "  <style>\n" +
                "    body {\n" +
                "      font-family: Arial, sans-serif;\n" +
                "      background-color: #f4f4f4;\n" +
                "      color: #333;\n" +
                "      margin: 0;\n" +
                "      padding: 0;\n" +
                "    }\n" +
                "    .container {\n" +
                "      max-width: 600px;\n" +
                "      margin: 30px auto;\n" +
                "      background-color: #ffffff;\n" +
                "      padding: 20px;\n" +
                "      border-radius: 8px;\n" +
                "      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);\n" +
                "    }\n" +
                "    .header {\n" +
                "      text-align: center;\n" +
                "      margin-bottom: 20px;\n" +
                "    }\n" +
                "    .header h2 {\n" +
                "      color: #e74c3c;\n" +
                "    }\n" +
                "    .content {\n" +
                "      line-height: 1.6;\n" +
                "    }\n" +
                "    .product-list {\n" +
                "      padding-left: 20px;\n" +
                "    }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <div class=\"container\">\n" +
                "    <div class=\"header\">\n" +
                "      <h2>Your Shop Has Been Banned</h2>\n" +
                "    </div>\n" +
                "    <div class=\"content\">\n" +
                "      <p>Dear " + shop.getShopName() + ",</p>\n" +
                "      <p>Your shop <strong>" + shop.getShopName() + "</strong> has been banned due to multiple violations.</p>\n" +
                "      <p>Below is the list of violated products:</p>\n" +
                "      <ul class=\"product-list\">\n" +
                "        " + productListHtml + "\n" +
                "      </ul>\n" +
                "      <p>Please contact our support team for further assistance.</p>\n" +
                "      <p>Thank you,<br>The Team</p>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</body>\n" +
                "</html>";

        emailService.sendEmail(shop.getAccount().getEmail(), "Shop Banned Notification", emailContent);
    }


    @Override
    public ApiResponse<List<ReportResponse>> getAllReport() {
        try {
            List<Report> reportPage = reportRepository.findAllByCreatedAtAndDesc();
            List<ReportResponse> reportResponses = reportMapper.toResponseDetail(reportPage);

            return ApiResponse.<List<ReportResponse>>builder()
                    .data(reportResponses)
                    .message("Reports retrieved successfully")
                    .build();

        } catch (Exception e) {
            return ApiResponse.<List<ReportResponse>>builder()
                    .message("Failed to retrieve reports: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<ReportResponse> getReportById(Long id) {
        try{
            Report report = reportRepository.getById(id);
            if(report==null){
                throw new AppException(ErrorCode.REPORT_NOT_FOUND);
            }
            ReportResponse reportResponse = reportMapper.toResponse(report);
            return ApiResponse.<ReportResponse>builder()
                    .data(reportResponse)
                    .message("Reports retrieved successfully")
                    .build();
        }catch (AppException e) {
            return ApiResponse.<ReportResponse>builder()
                    .code(e.getErrorCode().getCode())
                    .message(e.getMessage())
                    .build();
        } catch (Exception e) {
            return ApiResponse.<ReportResponse>builder()
                    .code(101)
                    .message("Failed: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<ShopResponse>> getShopBanned(Pageable pageable) {
        try {
            List<Shop> unapprovedShops = shopRepository.findUnapprovedShops(pageable);
            if (unapprovedShops.isEmpty()) {
                return ApiResponse.<List<ShopResponse>>builder()
                        .code(204)
                        .message("No unapproved shops found.")
                        .build();
            }
            List<ShopResponse> shopResponses = unapprovedShops.stream()
                    .map(shopMapper::toResponse)
                    .toList();
            return ApiResponse.<List<ShopResponse>>builder()
                    .code(200)
                    .data(shopResponses)
                    .message("Unapproved shops fetched successfully.")
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<ShopResponse>>builder()
                    .code(500)
                    .message("Error fetching unapproved shops: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public ApiResponse<List<ProductResponse>> getViolatedProductsByShop(Long shopId, Pageable pageable) {
        try{
            List<Report> approvedReports = reportRepository.findByStatusAndProduct_Shop_Id(RESOLVED, shopId,pageable);
            List<ProductEntity> violatedProducts = approvedReports.stream()
                    .map(Report::getProduct)
                    .distinct()
                    .toList();
            List<ProductResponse> productResponses = violatedProducts.stream()
                    .map(productMapper::convertToResponse)
                    .toList();
            return ApiResponse.<List<ProductResponse>>builder()
                    .data(productResponses)
                    .message("List of violated products retrieved successfully for shop " + shopId)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<List<ProductResponse>>builder()
                    .message("Failed to retrieve violated products: " + e.getMessage())
                    .build();
        }
    }

}
