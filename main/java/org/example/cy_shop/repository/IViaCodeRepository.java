package org.example.cy_shop.repository;

import org.example.cy_shop.entity.ViaCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IViaCodeRepository extends JpaRepository<ViaCode,Long> {
    @Query(value = "select v from ViaCode v where v.email = :email order by v.createdAt desc limit 1")
    ViaCode findByEmail(@Param("email") String email);
}
