package org.example.cy_shop.mapper;

import org.example.cy_shop.dto.request.auth.UserRequest;
import org.example.cy_shop.dto.response.auth.UserResponse;
import org.example.cy_shop.entity.*;
import org.example.cy_shop.exception.AppException;
import org.example.cy_shop.exception.ErrorCode;
import org.example.cy_shop.repository.IRoleRepository;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    @Autowired
    IRoleRepository roleRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    public UserResponse toResponse(Account account) {
        return UserResponse.builder()
                .username(account.getUser().getUsername())
                .accountId(account.getId())
                //.shopId(account.getShop().getId())
                .name(account.getUser().getName())
                .avatar(account.getUser().getAvatar())
                .gender(account.getUser().getGender())
                .location(account.getUser().getLocation())
                .dob(account.getUser().getDob())
                .email(account.getEmail())
                .role(account.getUserRoles().stream()
                        .map(userRole -> userRole.getRole().getName())
                        .collect(Collectors.toList()))
                .build();
    }

    public User convertToUserEntity(UserRequest userRequest){
        return User.builder()
                .username(userRequest.getUsername())
                .name(userRequest.getName())
                .avatar(Const.URL_IMAGE_USER_DEFAULT)
                .createdAt(userRequest.getCreatedAt())
                .updatedAt(userRequest.getUpdatedAt())
                .build();
    }

//    public LocationEntity convertToLocationEntity(UserRequest request){
//        if(request == null || request.getLocationUsers() == null)
//            return null;
//        return LocationEntity.builder()
//                .province(request.getLocationUsers().getProvince())
//                .district(request.getLocationUsers().getDistrict())
//                .ward(request.getLocationUsers().getWard())
//                .detailedAddress(request.getLocationUsers().getDetailedAddress())
//                .build();
//    }

    public Account convertToAcountEntity(UserRequest userRequest){
        try {
            User user = new User();
            Set<Role> role = new HashSet<>();

            if(userRequest != null && userRequest.getId() != null)
                user.setId(userRequest.getId());

            if(userRequest != null && userRequest.getRole() != null){
                for(var it: userRequest.getRole()) {
                    Role roleTmp = roleRepository.findByName(it);
                    if(roleTmp == null)
                        throw new AppException(ErrorCode.ROLE_NOT_FOUND);
                    role.add(roleTmp);
                }
            }

            Account account = new Account();
            account.setUsername(userRequest.getUsername());
            account.setUser(user);
            account.setIsActive(true);
            account.setEmail(userRequest.getEmail());
            account.setPassword(passwordEncoder.encode(userRequest.getPassword()));
            account.setCreatedAt(userRequest.getCreatedAt());
            account.setUpdatedAt(userRequest.getUpdatedAt());

            Set<UserRole> userRoles = new HashSet<>();
            for(var it: role) {
                UserRole userRoleTmp = new UserRole();
                userRoleTmp.setAccount(account);
                userRoleTmp.setRole(it);
                userRoles.add(userRoleTmp);
            }

            account.setUserRoles(userRoles);

            return account;
        }catch (Exception e){
            System.out.println("Lỗi convert userRequest sang account entity (user mapper) " + e);
            return null;
        }
    }

}