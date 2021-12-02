package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.xiliulou.afterserver.entity.Role;
import com.xiliulou.afterserver.entity.UserRole;
import com.xiliulou.afterserver.mapper.UserRoleMapper;
import com.xiliulou.afterserver.service.RoleService;
import com.xiliulou.afterserver.service.UserRoleService;
import com.xiliulou.core.utils.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service("userService")
public class UserRoleServiceImpl implements UserRoleService {

    @Resource
   private UserRoleMapper userRoleMapper;
   @Autowired
   private RoleService roleService;

    @Override
    public UserRole insert(UserRole userRole) {
        this.userRoleMapper.insert(userRole);
        return userRole;
    }

    @Override
    public List<Role> findByUid(Long uid) {
        List<UserRole> userRoles = this.userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUid, uid));
        if (!DataUtil.collectionIsUsable(userRoles)) {
            return null;
        }
        ArrayList<Role> useRoles = Lists.newArrayList();
        userRoles.forEach(e -> {
            Role role = this.roleService.findRoleById(e.getRid());
            if (Objects.isNull(role)) {
                return;
            }
            useRoles.add(role);
        });

        return useRoles;
    }

    @Override
    public boolean existsRole(Long id) {
        Integer count = userRoleMapper.selectCount(new LambdaQueryWrapper<UserRole>().eq(UserRole::getRid, id));
        return count != null && count > 0;
    }

    @Override
    public boolean deleteByUid(Long uid) {
        return userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUid, uid)) > 0;
    }
}
