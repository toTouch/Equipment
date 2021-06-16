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
                              @RequestParam(value = "dateType") Integer dateType){
        return dataQueryService.installWorkOrder(pointId,cityId,dateType);
    }

//    @GetMapping("/after/ratio")
//    public R afterRatio(@RequestParam(value = "pointId",required = false) Long pointId,
//                        @RequestParam(value = "cityId",required = false) Integer cityId,
//                        @RequestParam(value = "dateType") Integer dateType){
//        return dataQueryService.afterRatio(pointId,cityId,dateType);
//    }

    @GetMapping("/after")
    public R after(@RequestParam(value = "pointId",required = false) Long pointId,
                   @RequestParam(value = "cityId",required = false) Integer cityId,
                   @RequestParam(value = "dateType") Integer dateType){
        return dataQueryService.after(pointId,cityId,dateType);
    }

    @GetMapping("/quality/analyse")
    public R qualityAnalyse(@RequestParam(value = "pointId",required = false) Long pointId,
                            @RequestParam(value = "cityId",required = false) Integer cityId,
                            @RequestParam(value = "dateType") Integer dateType){
        return dataQueryService.qualityAnalyse(pointId,cityId,dateType);
    }

    @GetMapping("/quality/analyse/list")
    public R qualityAnalyseList(@RequestParam(value = "pointId",required = false) Long pointId,
                            @RequestParam(value = "cityId",required = false) Integer cityId,
                            @RequestParam(value = "dateType") Integer dateType){
        return dataQueryService.qualityAnalyseList(pointId,cityId,dateType);
    }


}
