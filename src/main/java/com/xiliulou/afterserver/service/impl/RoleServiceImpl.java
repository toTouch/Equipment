package com.xiliulou.afterserver.service.impl;

import com.google.common.collect.Lists;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.mapper.RoleMapper;
import com.xiliulou.afterserver.service.PermissionResourceService;
import com.xiliulou.afterserver.service.RoleService;
import com.xiliulou.afterserver.service.UserRoleService;
import com.xiliulou.afterserver.service.UserService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.util.TreeUtils;
import com.xiliulou.afterserver.web.query.RoleQuery;
import com.xiliulou.core.json.JsonUtil;
import com.xiliulou.core.utils.DataUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service("roleService")
public class RoleServiceImpl implements RoleService {
    @Resource
    RoleMapper roleMapper;
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    UserService userService;
    @Autowired
    PermissionResourceService permissionResourceService;

    @Override
    public Role insert(Role role) {
     this.roleMapper.insert(role);
     return role;
    }

    @Override
    public Integer update(Role role) {
      return this.roleMapper.update(role);
    }



    @Override
    public Role findRoleById(Long id) {
        return roleMapper.findRoleById(id);
    }

    @Override
    public List<Role> findRoleAll() {
       List<Role> roles=this.roleMapper.findRoleAll();
       if (!DataUtil.collectionIsUsable(roles)){
           return Collections.EMPTY_LIST;
       }
        //不显示超级管理员角色
       return roles.stream().filter(e -> e.getId() > 1).collect(Collectors.toList());
    }

    @Override
    public Boolean deleteById(Long id) {
        return roleMapper.deleteById(id)>0;
    }

    @Override
    public Long findByName(String name) {
        Role role = roleMapper.findByName(name);
      if (Objects.isNull(role)){
           return null;
      }
      return role.getId();
    }

    @Override
    public R addRole(RoleQuery roleQuery) {
        Role role = new Role();
        BeanUtils.copyProperties(roleQuery, role);

        role.setUpdateTime(System.currentTimeMillis());
        role.setCreateTime(System.currentTimeMillis());

        int insert = roleMapper.addRole(role);
        return insert > 0 ? R.ok() : R.fail("保存失败!");
    }

    @Override
    public R updateRole(RoleQuery roleQuery) {
        Role role = new Role();
        BeanUtils.copyProperties(roleQuery, role);

        role.setUpdateTime(System.currentTimeMillis());

        Integer update = update(role);
        return update > 0 ? R.ok() : R.fail("更新失败！");
    }


    @Override
    public Pair<Boolean, Object> findBindUid(Long uid) {
        List<Long> ids = queryRidsByUid(uid);
        return Pair.of(true, ids);
    }

    @Override
    public List<Long> queryRidsByUid(Long uid) {
        List<Role> roles = userRoleService.findByUid(uid);
        if (DataUtil.collectionIsUsable(roles)) {
            List<Long> rolesIds = roles.stream().map(Role::getId).collect(Collectors.toList());
            return rolesIds;
        }
        return Collections.emptyList();
    }

    @Override
    public Pair<Boolean, Object> deleteRole(Long id) {
        Role role = findRoleById(id);
        if (Objects.isNull(role)) {
            return Pair.of(false, "该id的角色不存在!");
        }

        if (userRoleService.existsRole(id)) {
            return Pair.of(false, "无法删除！请先解绑绑定该角色的用户");
        }

        if (deleteById(id)) {
            return Pair.of(true, null);
        }

        return Pair.of(false, "删除失败!");
    }

    @Override
    public Pair<Boolean, Object> bindUserRole(Long uid, List<Long> roleIds) {
        User user = userService.getUserById(uid);
        if (Objects.isNull(user)) {
            return Pair.of(false, "用户不存在，无法绑定！");
        }
        userRoleService.deleteByUid(uid);

        List<Role> roles = this.roleMapper.queryByRoleIds(roleIds);
        if (!DataUtil.collectionIsUsable(roles)) {
            return Pair.of(false, "角色不存在，无法绑定！");
        }

        roles.parallelStream().forEach(r -> {
            UserRole userRole = UserRole.builder()
                    .uid(uid)
                    .rid(r.getId())
                    .build();
            userRoleService.insert(userRole);
        });
        return Pair.of(true, "绑定成功!");
    }

    @Override
    public Pair<Boolean, Object> getMenuByUid() {
        Long uid = SecurityUtils.getUid();
        if (Objects.isNull(uid)) {
            return Pair.of(false, "未能查到相关用户！");
        }

        List<Long> rids = queryRidsByUid(uid);
        if (!DataUtil.collectionIsUsable(rids)) {
            return Pair.of(true, Collections.emptyList());
        }

        ArrayList<PermissionResource> result = Lists.newArrayList();

        for (Long rid : rids) {
            List<PermissionResource> permissionResources = permissionResourceService.findPermissionsByRole(rid);
            if (!DataUtil.collectionIsUsable(permissionResources)) {
                continue;
            }
            result.addAll(permissionResources.stream().filter(e -> e.getType().equals(PermissionResource.TYPE_PAGE)).sorted(Comparator.comparing(PermissionResource::getSort)).collect(Collectors.toList()));
        }

        List<PermissionResourceTree> permissionResourceTrees = TreeUtils.buildTree(result, PermissionResource.MENU_ROOT);
        return Pair.of(true, permissionResourceTrees);
    }
}
