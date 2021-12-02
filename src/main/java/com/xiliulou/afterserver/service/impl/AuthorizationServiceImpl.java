package com.xiliulou.afterserver.service.impl;

import com.google.common.collect.Lists;
import com.xiliulou.afterserver.entity.PermissionResource;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.service.AuthorizationService;
import com.xiliulou.afterserver.service.PermissionResourceService;
import com.xiliulou.afterserver.service.RoleService;
import com.xiliulou.afterserver.service.UserRoleService;
import com.xiliulou.core.utils.DataUtil;
import com.xiliulou.security.bean.UrlGrantedAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class AuthorizationServiceImpl implements AuthorizationService {

    @Autowired
    UserRoleService userRoleService;
    @Autowired
    RoleService roleService;
    @Autowired
    PermissionResourceService permissionResourceService;


    @Override
    public Collection<? extends GrantedAuthority> acquireAllAuthorities(long uid, int type) {
        if (type== User.TYPE_USER_SUPER){
           return Lists.newArrayList();
        }
        HashSet<GrantedAuthority> grantedAuthorities = new HashSet<>();
        List<Long> roleIds = roleService.queryRidsByUid(uid);
        if (!DataUtil.collectionIsUsable(roleIds)) {
            return grantedAuthorities;
        }

        for (Long roleId : roleIds) {
            List<PermissionResource> permissionResources = permissionResourceService.findPermissionsByRole(roleId);
            if (DataUtil.collectionIsUsable(permissionResources)) {
                for (PermissionResource p : permissionResources) {
                    //页面不需要校验
                    if (p.getType().equals(PermissionResource.TYPE_PAGE)) {
                        continue;
                    }
                    GrantedAuthority t = new UrlGrantedAuthority(p.getMethod(), p.getUri());
                    grantedAuthorities.add(t);
                }
            }
        }

        return grantedAuthorities;

    }
}
