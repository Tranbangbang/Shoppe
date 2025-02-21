package org.example.cy_shop.mapper;

import org.example.cy_shop.dto.response.auth.RoleResponse;
import org.example.cy_shop.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {
    public RoleResponse convertToResponse(Role role){
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }
}
