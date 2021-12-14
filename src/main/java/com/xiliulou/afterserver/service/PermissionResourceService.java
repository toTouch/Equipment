package com.xiliulou.afterserver.service;

import com.xiliulou.afterserver.entity.PermissionResource;
import com.xiliulou.afterserver.web.query.PermissionResourceQuery;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public interface PermissionResourceService {

    /**新增数据*/
    PermissionResource insert(PermissionResource permissionResource);

   /**修改数据*/
  Integer update(PermissionResource permissionResource);

  /**删除数据*/
  Boolean deleteById(Long id);

    Pair<Boolean, Object> getList();

    Pair<Boolean, Object> addPermissionResource(PermissionResourceQuery permissionResourceQuery);

    PermissionResource queryByIdFromCache(Long id);

    Pair<Boolean, Object> updatePermissionResource(PermissionResourceQuery permissionResourceQuery);

    Pair<Boolean, Object> deletePermission(Long permissionId);

    Pair<Boolean, Object> bindPermissionToRole(Long rid, List<Long> pid);

    Pair<Boolean, Object> getPermissionsByRole(Long rid);

    List<PermissionResource> findPermissionsByRole(Long rid);

}
