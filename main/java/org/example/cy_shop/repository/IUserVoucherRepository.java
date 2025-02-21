package org.example.cy_shop.repository;

import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.UserVoucher;
import org.example.cy_shop.entity.Voucher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IUserVoucherRepository extends JpaRepository<UserVoucher,Long> {
    Optional<UserVoucher> findByAccountAndVoucher(Account account, Voucher voucher);
    List<UserVoucher> findByAccount(Account account, Pageable pageable);

    @Query("SELECT uv FROM UserVoucher uv " +
            "JOIN uv.voucher v " +
            "WHERE uv.account.id = :accountId AND v.shop.id = :shopId")
    List<UserVoucher> findUserVouchersByAccountIdAndShopId(@Param("accountId") Long accountId,
                                                           @Param("shopId") Long shopId);
}
