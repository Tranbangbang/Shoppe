package org.example.cy_shop.service;

import org.example.cy_shop.dto.response.auth.RoleResponse;

public interface IRoleService {
    RoleResponse findByName(String name);
}
