package com.xiliulou.afterserver.service.impl;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.xiliulou.afterserver.config.RolePermissionConfig;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.mapper.PermissionResourceMapper;
import com.xiliulou.afterserver.service.PermissionResourceService;
import com.xiliulou.afterserver.service.RolePermissionService;
import com.xiliulou.afterserver.service.RoleService;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.util.TreeUtils;
import com.xiliulou.afterserver.web.query.PermissionResourceQuery;
import com.xiliulou.core.utils.DataUtil;
import com.xiliulou.db.dynamic.annotation.DS;
import com.xiliulou.security.bean.SecurityUser;
import com.xiliulou.security.bean.TokenUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service("permissionResourceService")
@Slf4j
public class PermissionResourceServiceImpl implements PermissionResourceService {

    @Resource
    PermissionResourceMapper permissionResourceMapper;
   @Autowired
   RoleService roleService;
    @Autowired
    RolePermissionService rolePermissionService;
    @Autowired
    RolePermissionConfig rolePermissionConfig;

    @Override
    public PermissionResource insert(PermissionResource permissionResource) {
      this.permissionResourceMapper.insertOne(permissionResource);
      return permissionResource;
    }

    @Override
    public Integer update(PermissionResource permissionResource) {
        return permissionResourceMapper.update(permissionResource);
    }

    @Override
    public Boolean deleteById(Long id) {
        return permissionResourceMapper.deleteById(id)>0;
    }

    @Override
    //@DS("slave_1")
    public Pair<Boolean, Object> getList() {
        TokenUser userInfo = SecurityUtils.getUserInfo();
        List<PermissionResource> permissionResources = this.permissionResourceMapper.queryAll();
        if (!DataUtil.collectionIsUsable(permissionResources)) {
            return Pair.of(false, "查询不到任何权限！");
        }

        //如果不是超级管理员，就不用返回前4个权限
        if (userInfo.getUid() > 3L) {
            permissionResources = permissionResources.stream().filter(e -> e.getId() > 4&& !rolePermissionConfig.getUnShow().contains(e.getId())).collect(Collectors.toList());
        }

        List<PermissionResourceTree> permissionResourceTrees = TreeUtils.buildTree(permissionResources, PermissionResource.MENU_ROOT);


        return Pair.of(true, permissionResourceTrees);
    }

    @Override
    public Pair<Boolean, Object> addPermissionResource(PermissionResourceQuery permissionResourceQuery) {
        Long uid = SecurityUtils.getUid();

        PermissionResource permissionResource = new PermissionResource();
        BeanUtils.copyProperties(permissionResourceQuery, permissionResource);

        //检查父元素是否存在
        if (permissionResource.getParent() != 0) {
            PermissionResource parentResource = queryByIdFromDB(permissionResource.getParent());
            if (Objects.isNull(parentResource)) {
                log.error("PERMISSION ERROR! permission no parent!,uid={},parentId={}", uid, permissionResource.getParent());
                return Pair.of(false, "父元素不存在");
            }
        }

        if (Objects.equals(permissionResourceQuery.getType(), PermissionResource.TYPE_URL) && isIllegalMethod(permissionResource.getMethod())) {
            return Pair.of(false, "方法不合法！");
        }

        permissionResource.setCreateTime(System.currentTimeMillis());
        permissionResource.setUpdateTime(System.currentTimeMillis());
        permissionResource.setDelFag(PermissionResource.DEL_NORMAL);

        PermissionResource insert = insert(permissionResource);

        return Objects.isNull(insert.getId()) ? Pair.of(false, "保存失败") : Pair.of(true, "保存成功");
    }

    @Override
    public PermissionResource queryByIdFromDB(Long id) {
        PermissionResource permissionResource = permissionResourceMapper.queryById(id);
        if (Objects.isNull(permissionResource)) {
            return null;
        }
        return permissionResource;
    }

    @Override
    public Pair<Boolean, Object> updatePermissionResource(PermissionResourceQuery permissionResourceQuery) {
        Long uid = SecurityUtils.getUid();

        PermissionResource permissionResource = new PermissionResource();
        BeanUtils.copyProperties(permissionResourceQuery, permissionResource);

        //检查父元素是否存在
        if (Objects.nonNull(permissionResource.getParent()) && permissionResource.getParent() != 0) {
            PermissionResource parentResource = queryByIdFromDB(permissionResource.getParent());
            if (Objects.isNull(parentResource)) {
                log.error("PERMISSION ERROR! permission no parent!,uid={},parentId={}", uid, permissionResource.getParent());
                return Pair.of(false, "父元素不存在");
            }
        }

        if (Objects.nonNull(permissionResource.getMethod()) && Objects.equals(permissionResourceQuery.getType(), PermissionResource.TYPE_URL) && isIllegalMethod(permissionResource.getMethod())) {
            return Pair.of(false, "方法不合法！");
        }

        permissionResource.setUpdateTime(System.currentTimeMillis());
        Integer update = update(permissionResource);
        return update > 0 ? Pair.of(true, null) : Pair.of(false, "更新失败!");
    }

    @Override
    public Pair<Boolean, Object> deletePermission(Long permissionId) {
        PermissionResource permissionResource = queryByIdFromDB(permissionId);

        if (Objects.isNull(permissionResource)) {
            return Pair.of(false, "未能查到相关权限");
        }
        if (deleteById(permissionId)) {
            return Pair.of(true, null);
        }

        return Pair.of(false, "删除失败!");
    }

    @Override
    public Pair<Boolean, Object> bindPermissionToRole(Long rid, List<Long> pid) {
        Role role = roleService.findRoleById(rid);
        if (Objects.isNull(role)) {
            return Pair.of(false, "角色查询不到！");
        }
        //删除旧的
        rolePermissionService.deleteByRid(rid);

        List<PermissionResource> permissionResources =queryListByIds(pid);
        if (!DataUtil.collectionIsUsable(permissionResources)) {
            return Pair.of(false, "权限查询不到！");
        }

        HashSet<Long> result = Sets.newHashSet();

        permissionResources.parallelStream().forEach(e -> {
            RolePermission rolePermission = RolePermission.builder()
                    .cid(e.getId())
                    .rid(rid)
                    .build();
            rolePermissionService.insert(rolePermission);
            result.add(e.getId());
        });
        return Pair.of(true, null);
    }

    @Override
    public Pair<Boolean, Object> getPermissionsByRole(Long rid) {
        List<PermissionResource> permissionResources = findPermissionsByRole(rid);
        if (DataUtil.collectionIsUsable(permissionResources)) {
            return Pair.of(true, permissionResources.stream().map(PermissionResource::getId).collect(Collectors.toList()));
        }
        return Pair.of(true, Collections.emptyList());
    }

    @Override
    public List<PermissionResource> findPermissionsByRole(Long rid) {
        List<Long> permissionIds = rolePermissionService.findCidByRid(rid);
        ArrayList<PermissionResource> result = Lists.newArrayList();

        if (DataUtil.collectionIsUsable(permissionIds)) {
            permissionIds.forEach(e -> {
                PermissionResource permissionResource = queryByIdFromDB(e);
                if (Objects.nonNull(permissionResource)) {
                    result.add(permissionResource);
                }
            });
        }
        return result;
    }


    private List<PermissionResource> queryListByIds(List<Long> pid) {
        return this.permissionResourceMapper.findByIds(pid);
    }



    private boolean isIllegalMethod(String method) {
        switch (method.toUpperCase()) {
            case "GET":
                return false;
            case "DELETE":
                return false;
            case "PUT":
                return false;
            case "POST":
                return false;
        }
        return true;
    }
}
