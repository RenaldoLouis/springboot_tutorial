package com.maul.app.ws.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.maul.app.ws.io.entity.AuthorityEntity;
import com.maul.app.ws.io.entity.RoleEntity;
import com.maul.app.ws.io.entity.UserEntity;

public class UserPrincipal implements UserDetails {

    UserEntity userEntity;
    private String userId;

    public UserPrincipal(UserEntity userEntity) {
        this.userEntity = userEntity;
        this.userId = userEntity.getUserId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> authorities = new HashSet<>();
        Collection<AuthorityEntity> authorityEntities = new HashSet<>();

        // Get user Roles
        Collection<RoleEntity> roles = userEntity.getRoles();

        if (roles == null)
            return authorities;

        roles.forEach((role) -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
            authorityEntities.addAll(role.getAuthorities());
        });

        authorityEntities.forEach((authorityEntity) -> {
            authorities.add(new SimpleGrantedAuthority(authorityEntity.getName()));
        });

        return authorities;
    }

    public Collection<String> getRoles() {
        Collection<String> stringRoles = new ArrayList<>();
        // Get user Roles
        Collection<RoleEntity> roles = userEntity.getRoles();

        if (roles == null)
            return stringRoles;

        roles.forEach((role) -> {
            stringRoles.add(role.getName());
        });

        return stringRoles;
    }

    @Override
    public String getPassword() {
        return this.userEntity.getEncryptedPassowrd();
    }

    @Override
    public String getUsername() {
        return this.userEntity.getEmail();
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
        return this.userEntity.getEmailVerificationStatus();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
