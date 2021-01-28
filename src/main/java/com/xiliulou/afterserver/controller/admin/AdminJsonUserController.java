package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 18:11
 **/
@RestController
@Slf4j
public class AdminJsonUserController {

    @GetMapping("test")
    public R test() {
        return R.ok("nihao");
    }

}
