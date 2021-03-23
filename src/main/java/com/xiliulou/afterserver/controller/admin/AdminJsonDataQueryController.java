package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.service.DataQueryService;
import com.xiliulou.afterserver.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
/*
* 数据统计
* */
@RestController
@RequestMapping("/admin/Data/count")
public class AdminJsonDataQueryController {
    @Autowired
    private DataQueryService dataQueryService;

    public R getDataCount(@RequestParam Map<String, Object> params){

        dataQueryService.getDataCount(params);

        return R.ok(dataQueryService);
    }


}
