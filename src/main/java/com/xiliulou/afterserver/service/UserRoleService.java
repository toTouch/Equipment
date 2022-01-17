package com.xiliulou.afterserver.service;

import com.xiliulou.afterserver.entity.Role;
import com.xiliulou.afterserver.entity.UserRole;

import java.util.List;

public interface UserRoleService {

    /**添加用户角色*/
    UserRole insert(UserRole userRole);

    List<Role> findByUid(Long uid);

    boolean existsRole(Long id);

    boolean deleteByUid(Long uid);
}
