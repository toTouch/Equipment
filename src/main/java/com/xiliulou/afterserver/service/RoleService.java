package com.xiliulou.afterserver.service;

import com.xiliulou.afterserver.entity.Role;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.RoleQuery;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface RoleService{
    /**添加角色
     * @return*/
    Role insert(Role role);
    /**修改角色*/
    Integer update(Role role);
    /**根据id查找一行角色*/
    Role findRoleById(Long id);
    /**查找所有的角色*/
    List<Role> findRoleAll(Long offset, Long size);

    Boolean deleteById(Long id);

    Long findByName(String name);

    R addRole(RoleQuery roleQuery);

    R updateRole(RoleQuery roleQuery);

    Pair<Boolean, Object> findBindUid(Long uid);

    List<Long> queryRidsByUid(Long uid);

    Pair<Boolean, Object> deleteRole(Long id);

    Pair<Boolean, Object> bindUserRole(Long uid, List<Long> roleIds);

    public Pair<Boolean, Object> getMenuByUid();
}

