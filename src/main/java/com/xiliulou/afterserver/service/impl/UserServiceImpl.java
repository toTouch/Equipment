package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.Deliver;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.mapper.UserMapper;
import com.xiliulou.afterserver.service.UserService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.password.PasswordUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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

    @Override
    public Pair<Boolean, Object> register(User user) {
        User userDb = baseMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUserName, user.getUserName()));
        if (Objects.nonNull(userDb)) {
            return Pair.of(false, "用户名已存在!");
        }
        user.setRoleId(User.AFTER_USER_ROLE);
        user.setCreateTime(System.currentTimeMillis());
        user.setPassWord(PasswordUtils.encode(user.getPassWord()));
        baseMapper.insert(user);
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

        return R.ok(selectPage);
    }

    @Override
    public User findByUserName(String username) {
        return baseMapper.selectOne(new QueryWrapper<User>().eq("user_name",username));
    }
}
