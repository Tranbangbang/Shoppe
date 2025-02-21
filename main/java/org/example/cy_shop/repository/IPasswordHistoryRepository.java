package org.example.cy_shop.repository;

import org.example.cy_shop.entity.PasswordHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IPasswordHistoryRepository extends JpaRepository<PasswordHistory,Long> {
    PasswordHistory findTop1ByAccountIdOrderByCreatedAtDesc(Long accountId);
}
