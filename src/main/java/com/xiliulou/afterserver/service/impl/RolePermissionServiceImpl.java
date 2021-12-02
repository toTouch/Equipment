package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiliulou.afterserver.entity.RolePermission;
import com.xiliulou.afterserver.mapper.RolePermissionMapper;
import com.xiliulou.afterserver.service.RolePermissionService;
import com.xiliulou.cache.redis.RedisService;
import com.xiliulou.core.json.JsonUtil;
import com.xiliulou.core.utils.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service("rolePermissionService")
public class RolePermissionServiceImpl implements RolePermissionService {

    @Resource
   private RolePermissionMapper rolePermissionMapper;


    @Override
    public RolePermission insert(RolePermission rolePermission) {
        this.rolePermissionMapper.insert(rolePermission);
        return rolePermission;
    }

    @Override
    public List<Long> findCidByRid(Long rid) {
        List<RolePermission> rolePermissions = this.rolePermissionMapper.selectList(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRid, rid));
        if (!DataUtil.collectionIsUsable(rolePermissions)) {
            return null;
        }
        List<Long> cidResult = rolePermissions.stream().map(RolePermission::getCid).collect(Collectors.toList());
        return cidResult;
    }

    @Override
    public boolean deleteByRid(Long rid) {
        return rolePermissionMapper.deleteByRid(rid)>0;
    }
}
