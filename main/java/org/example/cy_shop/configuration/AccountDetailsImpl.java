package org.example.cy_shop.configuration;

import lombok.*;
import org.example.cy_shop.entity.Account;
import org.example.cy_shop.entity.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@Transactional
public class AccountDetailsImpl implements UserDetails {
    private Account account;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        account.getUserRoles().forEach(userRole -> {
            Role role = userRole.getRole();
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        });
        return authorities;
    }

    public Account getAccount() {
        return this.account;
    }

    @Override
    public String getPassword() {
        if (account == null || account.getPassword() == null) {
            return null;
        }
        return account.getPassword();
    }

    @Override
    public String getUsername() {
        if (account == null || account.getUser() == null) {
            return null;
        }
        return account.getUser().getUsername();
    }

    public String getRoleName() {
        Set<String> roleNames = new HashSet<>();
        account.getUserRoles().forEach(userRole -> roleNames.add(userRole.getRole().getName()));
        return String.join(", ", roleNames);
    }

    public String getEmail() {
        if (account == null || account.getEmail() == null) {
            return null;
        }
        return account.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
