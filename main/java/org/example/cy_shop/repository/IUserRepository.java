package org.example.cy_shop.repository;

import org.example.cy_shop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, String> {
    long count();
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    @Query("SELECT u FROM User u WHERE u.username <> :username")
    List<User> findAllExceptCurrentUser(String username);

    @Query("select us from User us where " +
            " (:username is null or us.username = :username) ")
    Page<User> findCustom(@Param("username") String username,
                          Pageable pageable);

}
