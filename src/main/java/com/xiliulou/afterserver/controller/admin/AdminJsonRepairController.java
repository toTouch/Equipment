package com.xiliulou.afterserver.controller.admin;


import com.xiliulou.afterserver.service.RepairService;
import com.xiliulou.afterserver.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/*
* 工单统计
* */
@RestController
@RequestMapping("admin/repair")
public class AdminJsonRepairController {

    @Autowired
    private RepairService repairService;

    public R getRepairCount(@RequestParam Map<String, Object> params) {

        repairService.getRepairCount(params);

        return R.ok();
    }

}
