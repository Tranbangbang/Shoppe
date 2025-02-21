package org.example.cy_shop.repository.product_repository;

import org.example.cy_shop.dto.response.product.ProductRecommendationResponse;
import org.example.cy_shop.dto.response.product.ProductStatisticsResponse;
import org.example.cy_shop.entity.Shop;
import org.example.cy_shop.entity.product.ProductEntity;
import org.example.cy_shop.entity.product.StockEntity;
import org.example.cy_shop.repository.order.IOrderRepository;
import org.example.cy_shop.service.order.IOrderService;
import org.example.cy_shop.service.product.IProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IProductRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {
    ProductEntity findByProductCode(String code);

    @Query("select prd from ProductEntity prd where " +
            "(:keySearch is null or prd.productName like concat('%', :keySearch, '%') or prd.productDescription like concat('%', :keySearch, '%')) " +
            "and (:categoryName is null or prd.category.name like concat('%', :categoryName, '%') " +
                            "or (select cate.name from CategoryEntity cate where cate.id = prd.category.idParent) like concat('%', :categoryName, '%')) " +
            "and (:startPrice is null or  exists (select st from StockEntity st where st.product.id = prd.id and st.price > :startPrice)) " +
            "and (:endPrice is null or exists (select st from StockEntity st where st.product.id = prd.id and st.price <:endPrice )) " +
            "and (true and exists (select st from StockEntity st where st.product.id = prd.id and st.quantity > 0)) " +
            "and (:active is null or prd.isActive = :active) ")
    Page<ProductEntity> findAllCustom(
            @Param("keySearch") String keySearch,
            @Param("categoryName") String categoryName,
            @Param("startPrice") Double startPrice,
            @Param("endPrice") Double endPrice,
            @Param("active") Boolean active,
            Pageable pageable);

    boolean existsById(Long id);

    @Query("select prd from ProductEntity  prd where prd.id = :id and prd.isActive = true and prd.shop.isApproved = true")
    ProductEntity findByIdAndActive(@Param("id") Long id);

    @Query("select prd from ProductEntity  prd where prd.shop.id = :idShop  " +
            " and prd.isBan = true " +
            " and prd.isDelete = false" +
            " and (:start is null or  date(prd.modifierDate) >= :start ) " +
            " and (:end is null or date(prd.modifierDate)  <=:end )")
    List<ProductEntity> getAllProductBan(@Param("idShop") Long idShop,
                                         @Param("start")LocalDate start,
                                         @Param("end") LocalDate end);


//    @Query("select distinct st.product from StockEntity st where st.product.shop.id = :idShop " +
//            " and st.quantity = 0 " +
//            " and st.product.isBan = false " +
//            " and st.product.isDelete = false " +
//            " and st.product.isActive = true ")
//    List<ProductEntity> getAllProductOutStock(@Param("idShop") Long idShop);

    @Query("SELECT prd FROM ProductEntity prd " +
            "JOIN prd.stock st " +
            "WHERE prd.shop.id = :idShop " +
            "AND prd.isBan = false " +
            "AND prd.isDelete = false " +
            "AND prd.isActive = true " +
            "GROUP BY prd.id " +
            "HAVING SUM(st.quantity) = 0")
    List<ProductEntity> getAllProductOutStock(@Param("idShop") Long idShop);



    @Query("select rp.product.id from Report rp where  rp.product.shop.id = :idShop " +
            " and (:start is null or date(rp.createdAt) >=:start) " +
            " and (:end is null or date(rp.createdAt) <=:end)")
    List<Long> getAllReportByDay(@Param("idShop") Long idShop,
                                 @Param("start") LocalDate start,
                                 @Param("end") LocalDate end);

    @Query("select prd from ProductEntity prd " +
            "join OrderDetailEntity od on od.product.id = prd.id " +
            "where (prd.isActive is null or prd.isActive = true )  " +
            " and ( prd.isBan is null  or prd.isBan = false ) " +
            " and (prd.isDelete is null or prd.isDelete = false) " +
            " and prd.shop.isApproved = true " +
            "group by prd.id " +
            "order by count(od) desc")
    Page<ProductEntity> getBestSeller(Pageable pageable);

    @Query("select prd from ProductEntity prd " +
            "where (prd.isDelete is null or prd.isDelete = false) " +
            "and (prd.isActive is null or prd.isActive = true) " +
            "and (prd.isBan is null or prd.isBan = false) " +
            " and prd.shop.isApproved = true ")

    Page<ProductEntity> getAllNews(Pageable pageable);

    List<ProductEntity> findByShopId(Long shopId);

    Long id(Long id);

//    IOrderRepository
//    IProductService


    @Query("SELECT p FROM ProductEntity p " +
            "JOIN StockEntity s ON p.id = s.product.id " +
            "WHERE (:keyword IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND p.isActive = true " +
            "AND p.isBan = false " +
            "AND p.isDelete = false " +
            "AND s.quantity > 0 " +
            "GROUP BY p.id")
    Page<ProductEntity> searchByKeywordAndFilters(
            @Param("keyword") String keyword,
            Pageable pageable);


    @Query("SELECT p FROM ProductEntity p " +
            "JOIN StockEntity s ON p.id = s.product.id " +
            "LEFT JOIN FeedBackEntity f ON f.orderDetail.product.id = p.id " +
            "WHERE (:keyword IS NULL OR LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:place IS NULL OR LOWER(p.shop.detailedAddress.province) = LOWER(:place)) " +
            "AND (:idCategory IS NULL OR p.category.id = :idCategory) " +
            "AND (:startPrice IS NULL OR s.price >= :startPrice) " +
            "AND (:endPrice IS NULL OR s.price <= :endPrice) " +
            "AND (:rating IS NULL OR (SELECT AVG(f.rating) FROM FeedBackEntity f WHERE f.orderDetail.product = p) >= :rating) " +
            "AND p.isActive = TRUE " +
            "AND p.isBan = FALSE " +
            "AND p.isDelete = FALSE " +
            "AND s.quantity > 0 " +
            "GROUP BY p.id ")
    Page<ProductEntity> searchByKeywordAndFilterss(
            @Param("keyword") String keyword,
            @Param("place") String place,
            @Param("idCategory") Long idCategory,
            @Param("startPrice") Double startPrice,
            @Param("endPrice") Double endPrice,
            @Param("rating") Double rating,
            Pageable pageable
    );




    @Query("SELECT new org.example.cy_shop.dto.response.product.ProductRecommendationResponse(p.id, p.productName, COUNT(pv.id)) " +
            "FROM ProductEntity p " +
            "LEFT JOIN ProductView pv ON pv.product.id = p.id " +
            "WHERE p.isActive = true " +
            "AND p.isDelete = false " +
            "AND p.isBan = false " +
            "AND LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "GROUP BY p.id, p.productName " +
            "ORDER BY COUNT(pv.id) DESC")
    List<ProductRecommendationResponse> findRecommendedProducts(@Param("keyword") String keyword, Pageable pageable);




}


