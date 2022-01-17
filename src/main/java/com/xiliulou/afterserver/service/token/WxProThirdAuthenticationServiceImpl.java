package com.xiliulou.afterserver.service.token;

import com.xiliulou.security.authentication.thirdauth.ThirdAuthenticationService;
import com.xiliulou.security.bean.SecurityUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @author Hardy
 * @date 2021/12/13 14:35
 * @mood
 */
@Service
@Slf4j
public class WxProThirdAuthenticationServiceImpl implements ThirdAuthenticationService {

    @Override
    public SecurityUser registerUserAndLoadUser(HashMap<String, Object> authMap) {
        return null;
    }
}
