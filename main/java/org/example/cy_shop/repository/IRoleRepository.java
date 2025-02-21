package org.example.cy_shop.repository;

import org.example.cy_shop.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;


public interface IRoleRepository extends JpaRepository<Role,Long> {
    Role findByName(String name);
}
