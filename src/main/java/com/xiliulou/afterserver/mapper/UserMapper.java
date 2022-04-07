package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.web.vo.UserPull;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 18:35
 **/
public interface UserMapper extends BaseMapper<User> {

    List<UserPull> typePull(@Param("username") String username, @Param("type") Integer type);
}
