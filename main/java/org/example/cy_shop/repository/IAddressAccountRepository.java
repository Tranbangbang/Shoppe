package org.example.cy_shop.repository;

import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.AddressAccount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IAddressAccountRepository extends JpaRepository<AddressAccount,Long> {
    List<AddressAccount> findByAccount(Account account, Pageable pageable);
    AddressAccount getById(Long id);
    Optional<AddressAccount> findById(Long id);
    void deleteById(Long id);
}
