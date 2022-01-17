package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.LoginInfo;
import com.xiliulou.afterserver.mapper.LoginInfoMapper;
import com.xiliulou.afterserver.service.LoginInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Hardy
 * @date 2021/12/13 15:55
 * @mood
 */
@Service("loginInfoService")
@Slf4j
public class LoginInfoServiceImpl extends ServiceImpl<LoginInfoMapper, LoginInfo> implements LoginInfoService {

    @Resource
    LoginInfoMapper loginInfoMapper;

    @Override
    public void insert(LoginInfo loginInfo) {
        loginInfoMapper.insert(loginInfo);
    }
}