package org.example.cy_shop.repository;

import org.example.cy_shop.entity.Shop;
import org.example.cy_shop.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface IVoucherRepository extends JpaRepository<Voucher,Long>, JpaSpecificationExecutor<Voucher> {
    List<Voucher> findByShop(Shop shop);
    List<Voucher> findByShopId(Long id);
}
