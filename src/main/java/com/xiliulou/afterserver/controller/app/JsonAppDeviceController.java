package com.xiliulou.afterserver.controller.app;

import com.xiliulou.afterserver.mapper.ProductNewMapper;
import com.xiliulou.afterserver.service.ProductNewService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.iot.service.RegisterDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/app/device")
public class JsonAppDeviceController {


    @Autowired
    private ProductNewService productNewService;


    @GetMapping("/pullDeviceMessage")
    public R pullDeviceMessage(@RequestParam(value = "sn", required = true)String sn){
        return productNewService.queryDeviceMessage(sn);
    }
    @GetMapping("/getDeviceMessage")
    public R getDeviceMessage(@RequestParam(value = "sn", required = true)String sn){
        return productNewService.getDeviceMessage(sn);
    }

    @GetMapping("/notifyUsed")
    public R notifyUsed(@RequestParam(value = "sn", required = true)String sn){
        return productNewService.updateUsedStatus(sn);
    }


}
