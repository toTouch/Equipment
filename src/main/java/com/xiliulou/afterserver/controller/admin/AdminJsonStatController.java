package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.service.StatService;
import com.xiliulou.afterserver.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/*
*点位统计
* */
@RestController
@RequestMapping("admin/stat")
public class AdminJsonStatController {

    @Autowired
    private StatService statService;

    @GetMapping("/count")
    public R getStat(@RequestParam Map<String, Object> params) {

        return R.ok(statService.getStat(params));
    }
}
