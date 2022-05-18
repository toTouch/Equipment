package com.xiliulou.afterserver.controller.outer;

import com.xiliulou.afterserver.config.AppConfig;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ApiRequestQuery;
import com.xiliulou.security.utils.EncryptUtil;
import com.xiliulou.storage.config.StorageConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author zgw
 * @date 2022/4/26 15:32
 * @mood
 */
@RestController
@RequestMapping("/outer/compression")
@Slf4j
public class JsonOuterCompressionController {

    @Autowired
    private AppConfig appConfig;
    @Autowired
    StorageConfig storageConfig;

    @PostMapping("/login")
    public R checkCompression(@RequestParam("username")String username, @RequestParam("password")String password){
        String decryptPassword = appConfig.getPassword();
        if(!Objects.equals(username, appConfig.getUsername()) || !Objects.equals(decryptPassword, password)) {
            return R.fail(null, "用户名或密码错误");
        }

        Map<String, Object> result = new HashMap(4);
        result.put("appId", appConfig.getAppId());
        result.put("appSecret", appConfig.getAppSecret());
        result.put("ossAccessKeyId", storageConfig.getAccessKeyId());
        result.put("ossAccessKeySecret", storageConfig.getAccessKeySecret());
        return R.ok(result);
    }
}
