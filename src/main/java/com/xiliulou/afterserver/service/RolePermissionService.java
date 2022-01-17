package com.xiliulou.afterserver.service;

import com.xiliulou.afterserver.entity.RolePermission;

import java.util.List;

public interface RolePermissionService {

    /**添加*/
    RolePermission insert(RolePermission rolePermission);
    /**查询*/
    List<Long> findCidByRid(Long rid);

    /**删除*/
    boolean deleteByRid(Long rid);


}
