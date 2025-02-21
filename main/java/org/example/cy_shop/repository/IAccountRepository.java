package org.example.cy_shop.repository;

import org.example.cy_shop.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IAccountRepository extends JpaRepository<Account,Long> {

//    @Query("select ac from Account ac where " +
//            "(:username is null or ac.user.username like concat('%', :username, '%')) and " +
//            "(:username is null or ac.email like concat('%', :username, '%' ) ) and " +
//            "( :fullname is null or ac.user.name like concat('%', :fullname, '%')) and " +
//            "( :fullname is null or ac.email like concat('%', :fullname, '%')) and " +
//            "(:role is null or :role in (select ur.role.name from UserRole ur where ur.account = ac)) and " +
//            "(:active is null or ac.isActive = :active)")

    @Query("SELECT ac FROM Account ac " +
            "where (:username IS NULL OR (ac.username LIKE CONCAT('%', :username, '%') or  ac.email like  concat('%', :username, '%') )) " +
            "AND (:fullname IS NULL OR (ac.user.name LIKE CONCAT('%', :fullname, '%') or ac.email like concat('%',:fullname ,'%')) ) " +
            "AND (:role IS NULL OR :role IN (SELECT ur.role.name FROM UserRole ur WHERE ur.account = ac)) " +
            "AND (:active IS NULL OR ac.isActive = :active)")

    Page<Account> findAccountCustom(@Param("username") String username,
                                    @Param("fullname") String fullName,
                                    @Param("role") String role,
                                    @Param("active") Boolean active,
                                    Pageable pageable);

    Optional<Account> findByUser_Username(String username);
    Optional<Account> findByUser_Id(Long userId);

    @Query("select ac from Account ac where ac.user.id = :userId")
    Account findByUserIdCustom(@Param("userId") Long id);

    boolean existsByEmail(String email);

    Optional<Account> findByEmail(String email);

    @Query("select ac from Account ac where ac.email = :email")
    List<Account> findByEmailCustom(@Param("email") String email);

    @Query("SELECT COUNT(s.id) FROM Account s WHERE s.isActive = true")
    Long countTotalAccount();

    //---thống kê
    @Query("select count (acc.id) from Account  acc where acc.isActive  = true ")
    Long findStaticAllActive();

    @Query("select count (acc.id) from Account  acc where acc.isActive  = false ")
    Long findStaticAllInActive();

    @Query("select count (acc.id) from Account  acc ")
    Long findStaticAllAccount();

    @Query("select count (acc.id) from Account  acc where acc.isActive  = true and DATE(acc.createdAt) = :today")
    Long findStaticAllNewAccount(@Param("today") LocalDate today);
}
