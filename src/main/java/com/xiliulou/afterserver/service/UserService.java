package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.util.R;
import org.apache.commons.lang3.tuple.Pair;

public interface UserService extends IService<User> {
    Pair<Boolean, Object> register(User user);

    Pair<Boolean, Object> login(User user);

    User getUserById(Long uid);

    R list(Long offset, Long size, String username);

    User findByUserName(String username);

    R typePull(String username, Integer type);

    R updateUser(User user);
}
