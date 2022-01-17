package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.LoginInfo;

/**
 * @author Hardy
 * @date 2021/12/13 15:55
 * @mood
 */
public interface LoginInfoService extends IService<LoginInfo> {

    void insert(LoginInfo loginInfo);
}