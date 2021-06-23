package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.service.DataQueryService;
import com.xiliulou.afterserver.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
/*
* 数据统计
* */
@RestController
@RequestMapping("/admin/data")
public class AdminJsonDataQueryController {

    @Autowired
    private DataQueryService dataQueryService;

    @GetMapping("/install/work/order")
    public R installWorkOrder(@RequestParam(value = "pointId",required = false) Long pointId,
                              @RequestParam(value = "cityId",required = false) Integer cityId,
                              @RequestParam(value = "startTime",required = false) Long startTime,
                              @RequestParam(value = "endTime",required = false) Long endTime){
        return dataQueryService.installWorkOrder(pointId,cityId,startTime,endTime);
    }


    @GetMapping("/after")
    public R after(@RequestParam(value = "pointId",required = false) Long pointId,
                   @RequestParam(value = "cityId",required = false) Integer cityId,
                   @RequestParam(value = "startTime",required = false) Long startTime,
                   @RequestParam(value = "endTime",required = false) Long endTime){
        return dataQueryService.after(pointId,cityId,startTime,endTime);
    }

    @GetMapping("/quality/analyse")
    public R qualityAnalyse(@RequestParam(value = "pointId",required = false) Long pointId,
                            @RequestParam(value = "cityId",required = false) Integer cityId,
                            @RequestParam(value = "startTime",required = false) Long startTime,
                            @RequestParam(value = "endTime",required = false) Long endTime){
        return dataQueryService.qualityAnalyse(pointId,cityId,startTime,endTime);
    }

    @GetMapping("/quality/analyse/list")
    public R qualityAnalyseList(@RequestParam(value = "pointId",required = false) Long pointId,
                            @RequestParam(value = "cityId",required = false) Integer cityId,
                            @RequestParam(value = "startTime",required = false) Long startTime,
                            @RequestParam(value = "endTime",required = false) Long endTime){
        return dataQueryService.qualityAnalyseList(pointId,cityId,startTime,endTime);
    }


}
