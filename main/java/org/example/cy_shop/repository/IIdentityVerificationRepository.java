package org.example.cy_shop.repository;

import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.IdentityVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IIdentityVerificationRepository extends JpaRepository<IdentityVerification,Long> {
    boolean existsByAccount(Account account);
    boolean existsByIdentityNumber(String nummber);
    Optional<IdentityVerification> findByAccount(Account account);
    Optional<IdentityVerification> findByAccountId(Long accountId);
}
