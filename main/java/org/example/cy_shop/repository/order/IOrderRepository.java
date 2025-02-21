package org.example.cy_shop.repository.order;

import org.example.cy_shop.dto.response.DailyRevenueResponse;
import org.example.cy_shop.dto.response.TopShopResponse;
import org.example.cy_shop.dto.response.product.ProductStatisticsResponse;
import org.example.cy_shop.dto.response.shop.ShopPopularityResponse;
import org.example.cy_shop.dto.response.shop.ShopRevenueDTO;
import org.example.cy_shop.dto.response.shop.ShopRevenueResponse;
import org.example.cy_shop.entity.order.OrderEntity;
import org.example.cy_shop.enums.order.StatusOrderEnum;
import org.example.cy_shop.enums.order.StatusPaymentEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IOrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {

    @Query("SELECT o FROM OrderEntity o JOIN o.orderDetail od JOIN od.product p " +
            "WHERE o.account.id = :userId AND " +
            "(:productName IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :productName, '%'))) " +
            "ORDER BY o.createDate DESC")
    List<OrderEntity> findByAccountIdAndProductName(@Param("userId") Long userId,
                                                    @Param("productName") String productName,
                                                    Pageable pageable);

    @Query("SELECT COUNT(o.id) " +
            "FROM OrderEntity o " +
            "WHERE o.statusOrder = :status " )
    Long countTotalOrders(
            @Param("status") StatusOrderEnum status
    );


    @Query("SELECT new org.example.cy_shop.dto.response.TopShopResponse(s.id, s.shopName, SUM(od.lastPrice)) " +
            "FROM OrderDetailEntity od " +
            "JOIN od.order o " +
            "JOIN Shop s ON s.id = o.idShop " +
            "WHERE o.statusOrder = :statusOrder " +
            "AND o.statusPayment = :statusPayment " +
            "AND DATE(o.createDate) = CURRENT_DATE " +
            "GROUP BY s.id, s.shopName " +
            "ORDER BY SUM(od.lastPrice) DESC")
    Page<TopShopResponse> getTopShopsToday(
            @Param("statusOrder") StatusOrderEnum statusOrder,
            @Param("statusPayment") StatusPaymentEnum statusPayment,
            Pageable pageable);


    @Query("SELECT new org.example.cy_shop.dto.response.TopShopResponse(s.id, s.shopName, SUM(od.lastPrice)) " +
            "FROM OrderDetailEntity od " +
            "JOIN od.order o " +
            "JOIN Shop s ON s.id = o.idShop " +
            "WHERE o.statusOrder = :statusOrder " +
            "AND o.statusPayment = :statusPayment " +
            "AND o.createDate BETWEEN :startDate AND :endDate " +
            "GROUP BY s.id, s.shopName " +
            "ORDER BY SUM(od.lastPrice) DESC")
    Page<TopShopResponse> getTopShopsByPeriod(
            @Param("statusOrder") StatusOrderEnum statusOrder,
            @Param("statusPayment") StatusPaymentEnum statusPayment,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);


    @Query("SELECT MONTH(o.createDate) AS month, SUM(od.lastPrice) AS totalRevenue " +
            "FROM OrderDetailEntity od " +
            "JOIN od.order o " +
            "WHERE o.statusOrder = :statusOrder " +
            "AND o.statusPayment = :statusPayment " +
            "AND YEAR(o.createDate) = :year " +
            "GROUP BY MONTH(o.createDate) " +
            "ORDER BY MONTH(o.createDate)")
    List<Object[]> getMonthlyRevenue(
            @Param("statusOrder") StatusOrderEnum statusOrder,
            @Param("statusPayment") StatusPaymentEnum statusPayment,
            @Param("year") int year);

    @Query("SELECT DATE(o.createDate) AS date, SUM(od.lastPrice) AS totalRevenue " +
            "FROM OrderDetailEntity od " +
            "JOIN od.order o " +
            "WHERE o.statusOrder = :statusOrder " +
            "AND o.statusPayment = :statusPayment " +
            "AND o.createDate BETWEEN :startDate AND :endDate " +
            "GROUP BY DATE(o.createDate) " +
            "ORDER BY DATE(o.createDate)")
    List<Object[]> getDailyRevenue(
            @Param("statusOrder") StatusOrderEnum statusOrder,
            @Param("statusPayment") StatusPaymentEnum statusPayment,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);


    @Query("SELECT new org.example.cy_shop.dto.response.product.ProductStatisticsResponse(p.productName, SUM(od.quantity)) " +
            "FROM OrderDetailEntity od " +
            "JOIN od.product p " +
            "JOIN od.order o " +
            "WHERE o.statusOrder = :statusOrder " +
            "AND o.statusPayment = :statusPayment " +
            "GROUP BY p.id, p.productName " +
            "ORDER BY SUM(od.quantity) DESC")
    List<ProductStatisticsResponse> getTopProducts(
            @Param("statusOrder") StatusOrderEnum statusOrder,
            @Param("statusPayment") StatusPaymentEnum statusPayment,
            Pageable pageable);

    @Query("SELECT o FROM OrderEntity o JOIN o.orderDetail od JOIN od.product p " +
            "WHERE o.account.id = :userId AND " +
            "(:productName IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :productName, '%'))) AND " +
            "(:status IS NULL OR o.statusOrder = :status) " +
            "ORDER BY o.createDate DESC")
    List<OrderEntity> findByAccountIdProductNameAndStatus(@Param("userId") Long userId,
                                                          @Param("productName") String productName,
                                                          @Param("status") StatusOrderEnum status,
                                                          Pageable pageable);


    @Query("SELECT o FROM OrderEntity o WHERE o.id = :id AND o.account.id = :userId")
    Optional<OrderEntity> findByIdAndAccountId(@Param("id") Long id,
                                               @Param("userId") Long userId);

//    @Query("select ord from OrderEntity ord where ord.account.id= :idAcc " +
//            " and exists (select odetail from ord.orderDetail odetail where odetail.id = :idDetail and odetail.feedBack is not  null ) ")
//    OrderEntity findByAccountAndIdOrderDetails(@Param("idAcc")Long idAcc,
//                                               @Param("idDetail")Long idDetail);

//    @Query("select ord from OrderEntity ord where ord.account.id= :idAcc " +
//            " and exists (select odetail from ord.orderDetail odetail where odetail.id = :idDetail and odetail.feedBack.size is not null ) ")
//    OrderEntity findByAccountAndIdOrderDetails(@Param("idAcc") Long idAcc,
//                                               @Param("idDetail")Long idDetail);





    @Query("select ord from OrderEntity ord where ord.idShop = :idShop " +
            " and ord.statusOrder = :status " +
            " and (:start is null  or date(ord.modifierDate) >= :start ) " +
            " and (:end is null  or date(ord.modifierDate) <= :end )")
    List<OrderEntity> listOrderByStatus(@Param("idShop") Long idShop,
                                        @Param("status") StatusOrderEnum status,
                                        @Param("start") LocalDate startDate,
                                        @Param("end") LocalDate endDate);

    List<OrderEntity> findByStatusOrderAndCreateDateBetween(StatusOrderEnum statusOrder, LocalDateTime startDate, LocalDateTime endDate);


    @Query("SELECT new org.example.cy_shop.dto.response.shop.ShopRevenueDTO(o.idShop, SUM(od.lastPrice)) " +
            "FROM OrderEntity o " +
            "JOIN o.orderDetail od " +
            "WHERE o.statusOrder = :statusOrder " +
            "AND o.createDate BETWEEN :startDate AND :endDate " +
            "GROUP BY o.idShop " +
            "ORDER BY SUM(od.lastPrice) DESC")
    Page<ShopRevenueDTO> getTop5Shops(@Param("statusOrder") StatusOrderEnum statusOrder,
                                      @Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate,
                                      Pageable pageable);


    @Query("SELECT new org.example.cy_shop.dto.response.shop.ShopRevenueDTO(o.idShop, SUM(od.lastPrice)) " +
            "FROM OrderEntity o " +
            "JOIN o.orderDetail od " +
            "WHERE o.statusOrder = :statusOrder " +
            "AND o.statusPayment = :statusPayment " +
            "AND o.createDate BETWEEN :startDate AND :endDate " +
            "GROUP BY o.idShop")
    List<ShopRevenueDTO> getShopRevenueByPeriod(@Param("statusOrder") StatusOrderEnum statusOrder,
                                                @Param("statusPayment") StatusPaymentEnum statusPayment,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);

    @Query("SELECT new org.example.cy_shop.dto.response.shop.ShopRevenueResponse(" +
            "SUM(od.lastPrice), " +
            "COUNT(o.orderCode), " +
            "SUM(CASE WHEN o.statusOrder = org.example.cy_shop.enums.order.StatusOrderEnum.CANCELED THEN 1 ELSE 0 END)) " +
            "FROM OrderEntity o " +
            "JOIN o.orderDetail od " +
            "WHERE o.idShop = :shopId " +
            "AND o.statusOrder = :statusOrder " +
            "AND o.createDate BETWEEN :startOfDay AND :endOfDay " +
            "GROUP BY o.idShop")
    ShopRevenueResponse getShopRevenue(@Param("shopId") Long shopId,
                                       @Param("statusOrder") StatusOrderEnum statusOrder,
                                       @Param("startOfDay") LocalDateTime startOfDay,
                                       @Param("endOfDay") LocalDateTime endOfDay);








    @Query("select ord from OrderEntity ord " +
            "where (:orderStatus is null or ord.statusOrder = :orderStatus) " +
            "and (:keySearch is null or ord.id in (" +
            "    select ordDt.order.id from OrderDetailEntity ordDt " +
            "    where ordDt.product.productName like %:keySearch%)) " +
            "and ord.idShop = :idShop")
    Page<OrderEntity> findOrderOfShop(@Param("orderStatus") StatusOrderEnum orderStatus,
                                      @Param("keySearch") String keySearch,
                                      @Param("idShop") Long idShop,
                                      Pageable pageable);


    //---tìm đơn hàng theo ngày (không có status )
//    @Query("select ord from OrderEntity ord " +
//            "where (1=1) " +
//            "and (:keySearch is null or ord.id in (" +
//            "    select ordDt.order.id from OrderDetailEntity ordDt " +
//            "    where ordDt.product.productName like %:keySearch%)) " +
//            "and ord.idShop = :idShop " +
//            " and (:startDate is null or ord.createDate >= :startDate) " +
//            " and (:endDate is null or ord.createDate <= :endDate)")
//    Page<OrderEntity> findOrderOfShopWithOutStatus(@Param("orderStatus") StatusOrderEnum orderStatus,
//                                                   @Param("keySearch") String keySearch,
//                                                   @Param("idShop") Long idShop,
//                                                   @Param("startDate") LocalDate startDate,
//                                                   @Param("endDate") LocalDate endDate,
//                                                   Pageable pageable);
//
//    //---tìm đơn hàng theo ngày (có status)
//    @Query("select ord from OrderEntity ord " +
//            "where ord.statusOrder = :orderStatus " +
//            "and (:keySearch is null or ord.id in (" +
//            "    select ordDt.order.id from OrderDetailEntity ordDt " +
//            "    where ordDt.product.productName like %:keySearch%)) " +
//            "and ord.idShop = :idShop " +
//            " and (:startDate is null or ord.tracking >= :startDate) " +
//            " and (:endDate is null or ord.createDate <= :endDate)")
//    Page<OrderEntity> findOrderOfShop(@Param("orderStatus") StatusOrderEnum orderStatus,
//                                      @Param("keySearch") String keySearch,
//                                      @Param("idShop") Long idShop,
//                                      @Param("startDate") LocalDate startDate,
//                                      @Param("endDate") LocalDate endDate,
//                                      Pageable pageable);


    @Query("SELECT o FROM OrderEntity o " +
            " WHERE o.account.id = :idAcc " +  // Lọc theo idAccount
            " AND EXISTS (SELECT od FROM o.orderDetail od WHERE od.product.id = :idPrd) " +
            " and (o.statusOrder = 'RECEIVED' or o.statusOrder = 'RETURNED') " +
            " order by o.createDate desc ")  // Kiểm tra có Product có id = idPrd
    List<OrderEntity> findOrderByProductAndAccount(@Param("idPrd") Long idPrd,
                                              @Param("idAcc") Long idAcc);


    @Query("select ord from OrderEntity ord where ord.statusOrder = 'PENDING' " +
            " and exists (select odt from ord.orderDetail odt where odt.product.id = :idPrd)")
    List<OrderEntity> findOrderPenddingByIdProduct(@Param("idPrd") Long idProduct);

    @Query("select ord from OrderEntity ord where ord.statusOrder = 'PENDING' and ord.typePayment != 'CASH_ON_DELIVERY'" +
            " and ord.statusPayment = 'UNPAID' ")
    List<OrderEntity> findAllNotPaid();

    @Query("SELECT COUNT(o) FROM OrderEntity o WHERE o.idShop = :shopId AND o.statusOrder = :status AND o.createDate BETWEEN :startDate AND :endDate")
    Long countConfirmedOrdersByShop(@Param("shopId") Long shopId, @Param("status") StatusOrderEnum status, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(od.lastPrice) FROM OrderDetailEntity od WHERE od.product.shop.id = :shopId AND od.order.statusOrder = :status AND od.order.createDate BETWEEN :startDate AND :endDate")
    Double calculateRevenueByShop(@Param("shopId") Long shopId, @Param("status") StatusOrderEnum status, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


}
