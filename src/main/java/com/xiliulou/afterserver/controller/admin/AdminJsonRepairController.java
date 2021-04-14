package com.xiliulou.afterserver.controller.admin;


import com.xiliulou.afterserver.service.RepairService;
import com.xiliulou.afterserver.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/*
* 工单统计
* */
@RestController
@RequestMapping("admin")
public class AdminJsonRepairController {

    @Autowired
    private RepairService repairService;

    @GetMapping("/repair")
    public R getRepairCount(@RequestParam Map<String, Object> params) {

        return R.ok(repairService.getRepairCount(params));
    }
    //图标显示,返回日期json数组,数量json数组
    /*@GetMapping("mouth/{begin}/{end}")
    public R showData(@PathVariable String begin, @PathVariable String end) {
        Map<String,Object> map = this.repairService.getShowData(begin,end);
        return R.ok().data(map);
    }*/
}
