package org.example.cy_shop.repository;

import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IShopRepository extends JpaRepository<Shop,Long> {
    @Query("select sh from Shop sh where sh.account.user.username = :username")
    Optional findByUserName(@Param("username") String userName);

    @Query("SELECT COUNT(s.id) FROM Shop s")
    Long countTotalShops();


    @Query("SELECT COUNT(DISTINCT s.id) " +
            "FROM Shop s " +
            "JOIN OrderEntity o ON o.idShop = s.id " +
            "WHERE s.isApproved = true " +
            "AND o.createDate BETWEEN :startDate AND :endDate")
    Long countActiveShops(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(DISTINCT s.id) " +
            "FROM Shop s " +
            "JOIN OrderEntity o ON o.idShop = s.id " +
            "WHERE s.isApproved = false " +
            "AND o.createDate BETWEEN :startDate AND :endDate")
    Long countInactiveShops(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);



    @Query("SELECT COUNT(s.id) " +
            "FROM Shop s " +
            "WHERE s.isApproved = true " +
            "AND s.isApproved = true " +
            "AND s.createdAt BETWEEN :startDate AND :endDate " +
            "AND s.id NOT IN ( " +
            "    SELECT DISTINCT o.idShop " +
            "    FROM OrderEntity o " +
            "    WHERE o.createDate < :startDate " +
            ")")
    Long countNewShops(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


    @Query("select sp from Shop sp where sp.account.email = :email")
    Shop findByEmail(@Param("email") String email);

    boolean existsByShopName(String name);
    Optional<Shop> findByAccount(Account account);
    Shop findByShopName(String name);
    Page<Shop> findAll(Pageable pageable);

    Page<Shop> findByShopNameContainingIgnoreCase(String shopName, Pageable pageable);

    boolean existsById(Long id);

    @Query("select sp.shopName from Shop sp where sp.id = :id")
    String getNameShop(@Param("id") Long id);

    @Query("SELECT s FROM Shop s WHERE LOWER(s.shopName) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            "AND s.isApproved = true")
    Page<Shop> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT s FROM Shop s WHERE s.isApproved = false")
    List<Shop> findUnapprovedShops(Pageable pageable);

    @Query("SELECT s.account FROM Shop s WHERE s.shopName = :shopName")
    Account findAccountByShopName(@Param("shopName") String shopName);
}
