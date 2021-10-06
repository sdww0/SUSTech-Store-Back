package com.susstore.config.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;


public class LoginUsers implements UserDetails {

    private UserDetails userDetails;
    private String salt;
    private Integer id;

    public LoginUsers(UserDetails userDetails,Integer id,String salt){
        this.userDetails = userDetails;
        this.salt = salt;
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String getSalt() {
        return salt;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userDetails.getAuthorities();
    }

    @Override
    public String getPassword() {
        return userDetails.getPassword();
    }

    @Override
    public String getUsername() {
        return userDetails.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return userDetails.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return userDetails.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return userDetails.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return userDetails.isEnabled();
    }
}
