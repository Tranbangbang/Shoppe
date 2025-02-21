package org.example.cy_shop.repository;

import org.example.cy_shop.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IRefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByAccountIdAndDeviceInfo(Long accountId, String deviceInfo);
    Optional<RefreshToken> findByToken(String token);
    RefreshToken getByToken(String token);
}
