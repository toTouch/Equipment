package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.service.MouthService;
import com.xiliulou.afterserver.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    //图标显示,返回日期json数组,数量json数组
    @GetMapping("mouth/{begin}/{end}")
    public R showData( @PathVariable String begin, @PathVariable String end) {
        Map<String,Object> map = this.mouthService.getShowData(begin,end);
        return R.ok().data(map);
    }
}
