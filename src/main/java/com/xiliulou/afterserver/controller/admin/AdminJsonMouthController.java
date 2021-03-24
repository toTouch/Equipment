package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.service.MouthService;
import com.xiliulou.afterserver.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/*
* 格口统计
* */
@RestController
@RequestMapping("admin")
public class AdminJsonMouthController {

    @Autowired
    private MouthService mouthService;

    @GetMapping("/mouth")
    public R getStat(@RequestParam Map<String, Object> params) {

        return R.ok(mouthService.getMouth(params));
    }
}
