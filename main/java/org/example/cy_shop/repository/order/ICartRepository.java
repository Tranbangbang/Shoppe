package org.example.cy_shop.repository.order;

import org.example.cy_shop.entity.order.CartEntity;
import org.example.cy_shop.enums.StatusCartEnum;
import org.example.cy_shop.service.impl.order.VNPayService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICartRepository extends JpaRepository<CartEntity, Long> {

    @Query("select car from CartEntity car where car.account.id = :idAccount")
    Page<CartEntity> findAllCustome(@Param("idAccount") Long idAccount,
                                    Pageable pageable);

    @Query("select ca from CartEntity  ca where ca.productId = :idProduct and ca.variant = :variant " +
            " and ca.account.id = :idAccount and ca.status = 'NORMAL'")
    CartEntity findByProductIdAndVariantAndIdAccount(@Param("idProduct") Long idProduct,
                                                     @Param("variant") String variant,
                                                     @Param("idAccount") Long idAccount);

    List<CartEntity> findByStatusAndProductId(StatusCartEnum status, Long idProduct);

    List<CartEntity> findByProductId(Long idProduct);
}

//VNPayService
