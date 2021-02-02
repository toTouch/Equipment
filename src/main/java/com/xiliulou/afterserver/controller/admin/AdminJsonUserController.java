package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.config.MinioConfig;
import com.xiliulou.afterserver.controller.BaseController;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.service.UserService;
import com.xiliulou.afterserver.util.MinioUtil;
import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 18:11
 **/
@RestController
@Slf4j
public class AdminJsonUserController extends BaseController {
    @Autowired
    UserService userService;

    @PostMapping("admin/register")
    public R register(@RequestBody User user) {

        return super.returnPairResult(userService.register(user));
    }

    @PostMapping("auth/login")
    public R login(@RequestBody User user) {
        return super.returnPairResult(userService.login(user));
    }

}
