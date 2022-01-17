package com.xiliulou.afterserver.service.token;

import com.xiliulou.afterserver.entity.PermissionResource;
import com.xiliulou.afterserver.service.PermissionResourceService;
import com.xiliulou.afterserver.service.RoleService;
import com.xiliulou.afterserver.service.UserRoleService;
import com.xiliulou.core.utils.DataUtil;
import com.xiliulou.security.authentication.authorization.AuthorizationService;
import com.xiliulou.security.bean.UrlGrantedAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * @author Hardy
 * @date 2021/12/13 14:55
 * @mood
 */
@Service
public class AuthorizationServiceImpl implements AuthorizationService {
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    RoleService roleService;
    @Autowired
    PermissionResourceService permissionResourceService;

    @Override
    public Collection<? extends GrantedAuthority> acquireAllAuthorities(long uid, int type) {

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
