package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.config.RolePermissionConfig;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.mapper.UserMapper;
import com.xiliulou.afterserver.service.ServerService;
import com.xiliulou.afterserver.service.SupplierService;
import com.xiliulou.afterserver.service.UserRoleService;
import com.xiliulou.afterserver.service.UserService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.util.password.PasswordUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 19:03
 **/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
   // @Autowired
    //JwtHelper jwtHelper;

    @Autowired
    RolePermissionConfig rolePermissionConfig;
    @Autowired
    UserRoleService userRoleService;
    @Autowired
    SupplierService supplierService;
    @Autowired
    ServerService serverService;

    @Override
    public Pair<Boolean, Object> register(User user) {
        User userDb = baseMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUserName, user.getUserName()));
        if (Objects.nonNull(userDb)) {
            return Pair.of(false, "用户名已存在!");
        }
        if(Objects.isNull(user.getUserType())){
            return Pair.of(false, "请填写用户类型!");
        }
        if(StringUtils.isBlank(user.getPassWord())){
            return Pair.of(false, "请填写合法密码");
        }
        if(Objects.equals(User.TYPE_FACTORY, user.getUserType())){
            Supplier supplier = supplierService.getById(user.getThirdId());
            if(Objects.isNull(supplier)){
                return Pair.of(false, "未查询到工厂，请检查");
            }
        }
        if(Objects.equals(User.TYPE_PATROL_APPLET, user.getUserType())) {
            Server server = serverService.getById(user.getThirdId());
            if(Objects.isNull(server)){
                return Pair.of(false, "未查询到服务商，请检查");
            }
        }
        user.setRoleId(User.AFTER_USER_ROLE);
        user.setPicture("1.npg");
        user.setCreateTime(System.currentTimeMillis());
        user.setPassWord(PasswordUtils.encode(user.getPassWord()));
        baseMapper.insert(user);

        List<Long> userRoles = user.getRids();
        if(!CollectionUtil.isEmpty(userRoles)){
            userRoles.stream().forEach(item -> {
                UserRole userRole = new UserRole();
                userRole.setUid(user.getId());
                userRole.setRid(item);
                userRoleService.insert(userRole);
            });
        }

        return Pair.of(true, null);
    }

    @Override
    public Pair<Boolean, Object> login(User user) {
        User userDb = baseMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUserName, user.getUserName()));
        if (Objects.isNull(userDb)) {
            return Pair.of(false, "用户不存在!");
        }
        log.info("pass:{}", user.getPassWord());
        Boolean passwordMatchResult = PasswordUtils.matches(user.getPassWord(), userDb.getPassWord());
        if (!passwordMatchResult) {
            return Pair.of(Boolean.FALSE, "密码错误!");
        }
        Map<String, Object> userClaims = new HashMap<>();
        userClaims.put("id", String.valueOf(userDb.getId()));
        userClaims.put("name", userDb.getUserName());
        userClaims.put("roleId", userDb.getRoleId());


        //JSONObject jsonToken = jwtHelper.generateToken(userClaims);
        return Pair.of(true, null);
    }

    @Override
    public User getUserById(Long uid) {

        return baseMapper.selectById(uid);
    }

    @Override
    public R list(Long offset, Long size, String username) {
        Page page = PageUtil.getPage(offset, size);
        Page selectPage = baseMapper.selectPage(page,Wrappers.<User>lambdaQuery().like(Objects.nonNull(username),User::getUserName, username));
        if(CollectionUtils.isNotEmpty(selectPage.getRecords())) {
            selectPage.getRecords().forEach(item -> {
                User user = (User)item;
                if(Objects.equals(user.getUserType(), User.TYPE_FACTORY)) {
                    Supplier supplier = supplierService.getById(user.getThirdId());
                    if(Objects.nonNull(supplier)) {
                        user.setThirdName(supplier.getName());
                    }
                }

                if(Objects.equals(user.getUserType(), User.TYPE_PATROL_APPLET)) {
                    Server server = serverService.getById(user.getThirdId());
                    if(Objects.nonNull(server)) {
                        user.setThirdName(server.getName());
                    }
                }
            });
        }
        return R.ok(selectPage);
    }

    @Override
    public User findByUserName(String username) {
        return baseMapper.selectOne(new QueryWrapper<User>().eq("user_name",username));
    }

    @Override
    public R typePull(String username, Integer type) {
        return R.ok(baseMapper.typePull(username, type));
    }
}
