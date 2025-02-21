package org.example.cy_shop.service.impl;

import org.example.cy_shop.configuration.jwtConfig.JwtProvider;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.follow.FollowRequest;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.Follower;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.repository.IAccountRepository;
import org.example.cy_shop.repository.IFollowRepository;
import org.example.cy_shop.repository.IShopRepository;
import org.example.cy_shop.service.IFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FollowServiceImpl implements IFollowService {
    @Autowired
    private IFollowRepository _followRepository;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private IAccountRepository accountRepository;
    @Autowired
    private IShopRepository shopRepository;
    @Override
    public ResponseEntity<ApiResponse<String>> follow(FollowRequest followRequest) {
        try {
            String email = jwtProvider.getEmailContext();
            if (email == null || email.isEmpty()) {
                throw new AppException(ErrorCode.INVALID_MISSING_TOKEN);
            }
            Account userToFollow = accountRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_NOT_FOUND));
            String username = userToFollow.getUsername();
            String shopName = shopRepository.getNameShop(Long.valueOf(followRequest.getShopId()));
            if(shopName==null){
                throw new AppException(ErrorCode.SHOP_NOT_FOUND);
            }
            Follower existingFollower = _followRepository.findByUsernameAndFollowedUsername(username, shopName);
            if (existingFollower != null) {
                existingFollower.setIs_active(false);
                existingFollower.setCreatedAt(LocalDateTime.now());
                _followRepository.save(existingFollower);
            } else {
                Follower newFollower = Follower.builder()
                        .username(username)
                        .followedUsername(shopName)
                        .is_active(true)
                        .createdAt(LocalDateTime.now())
                        .build();
                _followRepository.save(newFollower);
            }
            ApiResponse<String> response = ApiResponse.<String>builder()
                    .message("Follow operation completed successfully.")
                    .data("Success")
                    .build();
            return ResponseEntity.ok(response);
        } catch (AppException e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.<String>builder()
                            .message(e.getMessage())
                            .data(null)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.<String>builder()
                            .code(500)
                            .message("Failed : " + e.getMessage())
                            .build()
            );
        }
    }
}
