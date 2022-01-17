package com.xiliulou.afterserver.service;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface AuthorizationService {
    Collection<? extends GrantedAuthority> acquireAllAuthorities(long uid, int type);

}
