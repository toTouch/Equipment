package com.xiliulou.afterserver.service.impl;

import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.service.UserService;
import com.xiliulou.afterserver.util.SecurityUser;
import com.xiliulou.security.authentication.authorization.AuthorizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    UserService userService;
    @Autowired(required = false)
    AuthorizationService authorizationService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //从数据库获取用户信息
        User user=userService.findByUserName(username);
        if (user==null){
            throw new UsernameNotFoundException("用户名不存在!");
        }
        Collection<? extends GrantedAuthority> authorities=authorizationService.acquireAllAuthorities(user.getId(),user.getRoleId());

        return new SecurityUser(user.getId(),user.getUserName(),user.getPassWord(),user.getRoleId(),user.getCreateTime(),authorities);
    }
}
