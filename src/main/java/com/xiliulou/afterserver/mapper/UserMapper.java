package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.web.vo.UserPull;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 18:35
 **/
public interface UserMapper extends BaseMapper<User> {

    UserPull typePull(String username, Integer type);
}
