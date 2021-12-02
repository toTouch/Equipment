package com.xiliulou.afterserver.service.impl;

import com.xiliulou.afterserver.entity.Role;
import com.xiliulou.afterserver.mapper.RoleMapper;
import com.xiliulou.afterserver.service.RoleService;
import com.xiliulou.afterserver.service.UserRoleService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.RoleQuery;
import com.xiliulou.core.utils.DataUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service("roleService")
public class RoleServiceImpl implements RoleService {
    @Resource
    RoleMapper roleMapper;
    @Autowired
    UserRoleService userRoleService;

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
//       List<Role> roles=this.roleMapper.findRoleAll();
//       if (!DataUtil.collectionIsUsable(roles)){
//           return R.ok(Collections.EMPTY_LIST);
//       }
//       return R.ok();
        return this.roleMapper.findRoleAll();
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
    public Pair<Boolean, Object> findBindUidRids(Long uid) {
        List<Long> ids = (List<Long>) findBindUidRids(uid);
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
}
