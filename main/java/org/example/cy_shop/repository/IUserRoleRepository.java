package org.example.cy_shop.repository;

import org.example.cy_shop.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface IUserRoleRepository extends JpaRepository<UserRole, Long> {
    List<UserRole> findByAccountId(Long id);

//    @Query("select from ")
    List<UserRole> findByAccountUserName(String userName);
    @Query("SELECT ur.account.id FROM UserRole ur WHERE ur.role.id = 3")
    List<Long> findAdminAccountIds();
}
