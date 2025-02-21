package org.example.cy_shop.repository.order;

import org.example.cy_shop.dto.response.product.ProductStatisticsResponse;
import org.example.cy_shop.entity.order.OrderDetailEntity;
import org.example.cy_shop.enums.order.StatusOrderEnum;
import org.example.cy_shop.enums.order.StatusPaymentEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IOrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {

    @Query("SELECT SUM(od.lastPrice) " +
            "FROM OrderDetailEntity od " +
            "WHERE od.order.statusOrder = :status " +
            "AND od.order.statusPayment = :paymentStatus")
    Double calculateTotalRevenue(
            @Param("status") StatusOrderEnum status,
            @Param("paymentStatus") StatusPaymentEnum paymentStatus);



    @Query(value = "SELECT " +
            "p.id AS productId, " +
            "p.product_name AS productName, " +
            "p.product_code AS productCode, " +
            "MIN(od.price) AS minPrice, " +
            "MAX(od.price) AS maxPrice, " +
            "SUBSTRING_INDEX(GROUP_CONCAT(od.image ORDER BY od.id), ',', 1) AS image, " +
            "SUM(od.quantity) AS quantitySold " +
            "FROM team02.tbl_order_item od " +
            "JOIN team02.tbl_product p ON od.id_product = p.id " +
            "JOIN team02.tbl_order o ON od.id_order = o.id " +
            "WHERE o.status_order = :status " +
            "AND o.status_payment = :paymentStatus " +
            "AND o.create_date BETWEEN :startDate AND :endDate " +
            "AND p.shop_id = :shopId " +
            "GROUP BY p.id, p.product_name, p.product_code " +
            "ORDER BY quantitySold DESC",
            nativeQuery = true)
    List<Object[]> findTopSellingProductsByShop(
            @Param("status") String status,
            @Param("paymentStatus") String paymentStatus,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("shopId") Long shopId
    );



}
