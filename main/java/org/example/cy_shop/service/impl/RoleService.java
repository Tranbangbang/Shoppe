package org.example.cy_shop.service.impl;

import org.example.cy_shop.dto.response.auth.RoleResponse;
import org.example.cy_shop.mapper.RoleMapper;
import org.example.cy_shop.repository.IRoleRepository;
import org.example.cy_shop.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService implements IRoleService {
    @Autowired
    IRoleRepository roleRepository;
    @Autowired
    RoleMapper roleMapper;

    @Override
    public RoleResponse findByName(String name) {
        try {
            return roleMapper.convertToResponse(roleRepository.findByName(name));
        }catch (Exception e){
            System.out.println("Lỗi tìm role theo name(role service): " + e);
            return null;
        }
    }
}
