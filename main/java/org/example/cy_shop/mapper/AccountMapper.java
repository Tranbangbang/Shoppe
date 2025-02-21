package org.example.cy_shop.mapper;

import org.example.cy_shop.dto.response.auth.AccountResponse;
import org.example.cy_shop.entity.Account;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AccountMapper {

    public AccountResponse toResponse(Account account) {
        Long idUser = null;
        if(account != null && account.getUser()!= null)
            idUser = account.getUser().getId();

        String userName = null, name = null;
        if(account.getUser() != null && account.getUser().getUsername() != null)
            userName = account.getUser().getUsername();

        if(account.getUser() != null && account.getUser().getName() != null)
            name = account.getUser().getName();

        List<String> role = new ArrayList<>();
        if(account.getUserRoles() != null){
            role = account.getUserRoles().stream()
                        .map(userRole -> userRole.getRole().getName())
                        .collect(Collectors.toList());
        }

        return AccountResponse.builder()
                .idUser(idUser)
                .id(account.getId())
                .username( userName)
                .name(name)
                .avatar(account.getUser().getAvatar())
                .email(account.getEmail())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .isActive(account.getIsActive())
                .role(role)
//                .role(account.getUserRoles().stream()
//                        .map(userRole -> userRole.getRole().getName())
//                        .collect(Collectors.toList()))
                .build();
    }


}
