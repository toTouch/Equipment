package com.xiliulou.afterserver.controller.app;

import com.xiliulou.afterserver.service.ProductNewService;
import com.xiliulou.afterserver.web.query.CabinetCompressionQuery;
import com.xiliulou.afterserver.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zgw
 * @date 2023/4/26 10:00
 * @mood
 */
@RestController
@RequestMapping("/app/noScreen/compression")
public class JsonAppNoScreenCompressionController {

    @Autowired
    private ProductNewService productNewService;

    /**
     * 柜机压测状态上报
     * @return
     */

    @PostMapping("upload")
    public R cabinetCompressionStatus(@RequestBody CabinetCompressionQuery cabinetCompressionQuery){
        return productNewService.cabinetCompressionStatus(cabinetCompressionQuery);
    }

    @GetMapping("check")
    public R cabinetCompressionCheck(@RequestParam("sn")String sn){
        return productNewService.cabinetCompressionCheck(sn);
    }

    @GetMapping("list")
    public R cabinetCompressionList(@RequestParam(value = "sn", required = false)String sn,
        @RequestParam(value = "startTime", required = false) Long startTime,
        @RequestParam(value = "endTime", required =  false)Long endTime){
        return productNewService.cabinetCompressionList(sn, startTime, endTime);
    }
}
