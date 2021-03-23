package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.service.StatService;
import com.xiliulou.afterserver.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/*
*点位统计
* */
@RestController
@RequestMapping("admin/stat/count")
public class AdminJsonStatController {

    @Autowired
    private StatService statService;

    public R getStat(@RequestParam Map<String, Object> params) {

        statService.getStat(params);

        return R.ok(statService);
    }
}
