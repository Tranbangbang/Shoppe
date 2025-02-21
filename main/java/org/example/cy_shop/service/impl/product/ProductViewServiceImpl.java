package org.example.cy_shop.service.impl.product;

import jakarta.servlet.http.HttpServletRequest;
import org.example.cy_shop.configuration.jwtConfig.JwtProvider;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.product.ProductEntity;
import org.example.cy_shop.entity.product.ProductView;
import org.example.cy_shop.repository.IAccountRepository;
import org.example.cy_shop.repository.product_repository.IProductRepository;
import org.example.cy_shop.repository.product_repository.IProductViewRepository;
import org.example.cy_shop.service.IProductViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProductViewServiceImpl implements IProductViewService {
    @Autowired
    private IProductViewRepository productViewRepository;
    @Autowired
    private IProductRepository productRepository;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private IAccountRepository accountRepository;

    @Override
    public ResponseEntity<ApiResponse<Long>> recordProductView(Long productId, HttpServletRequest request) {
        try {
            String userIp = request.getRemoteAddr();
            Long viewCount = recordView(productId, userIp);
            return ResponseEntity.ok(
                    ApiResponse.<Long>builder()
                            .code(200)
                            .message("Product view recorded successfully.")
                            .data(viewCount)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<Long>builder()
                            .code(500)
                            .message("Failed to record product view: " + e.getMessage())
                            .build()
            );
        }
    }

    @Override
    public Long recordView(Long productId, String userIp) {
        try {
            String email = null;
            try {
                email = jwtProvider.getEmailContext();
            } catch (Exception jwtException) {
                email = null;
            }
            Optional<Account> account = email != null ? accountRepository.findByEmail(email) : Optional.empty();
            ProductEntity product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

            LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);

            boolean alreadyViewed = false;

            if (account.isPresent() && account.get().getId() != null) {
                alreadyViewed = productViewRepository.existsByProductAndUserIdAndViewedAtBetween(
                        product, account.get().getId(), startOfDay, endOfDay
                );
            } else if (userIp != null) {
                alreadyViewed = productViewRepository.existsByProductAndUserIpAndViewedAtBetween(
                        product, userIp, startOfDay, endOfDay
                );
            }

            if (!alreadyViewed) {
                ProductView productView = ProductView.builder()
                        .product(product)
                        .userId(account.isPresent() ? account.get().getId() : null)
                        .userIp(userIp)
                        .viewedAt(LocalDateTime.now())
                        .build();
                productViewRepository.save(productView);
            }

            return productViewRepository.countByProductAndViewedAtBetween(product, startOfDay, endOfDay);
        } catch (Exception e) {
            throw new RuntimeException("Failed to record product view: " + e.getMessage(), e);
        }
    }

}
