package com.xiliulou.afterserver.controller.app;

import com.xiliulou.afterserver.mapper.ProductNewMapper;
import com.xiliulou.afterserver.service.ProductNewService;
import com.xiliulou.afterserver.service.impl.DeliverServiceImpl;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.DeliverQuery;
import com.xiliulou.iot.service.RegisterDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/app/device")
public class JsonAppDeviceController {


    @Autowired
    private ProductNewService productNewService;
    
    @Autowired
    private DeliverServiceImpl deliverService;
    
    
    @GetMapping("/pullDeviceMessage")
    public R pullDeviceMessage(@RequestParam(value = "sn", required = true)String sn){
        return productNewService.queryDeviceMessage(sn);
    }
    @GetMapping("/getDeviceMessage")
    public R getDeviceMessage(@RequestParam(value = "sn", required = true)String sn){
        return productNewService.getDeviceMessage(sn);
    }
    
    /**
     * 绑定设备使用状态
     *
     * @param sn
     * @param cpuSerialNum
     * @return
     */
    @GetMapping("/notifyUsed")
    public R notifyUsed(@RequestParam(value = "sn", required = true)String sn, @RequestParam(value = "cpuSerialNum", required = false)String cpuSerialNum){
        return productNewService.updateUsedStatus(sn, cpuSerialNum);
    }
    
    /**
     * 解绑设备使用状态
     * 更改三元、cpu 序列号绑定情况
     * @param sn
     * @return
     */
    @GetMapping("/unbundled")
    public R unbundled(@RequestParam(value = "sn", required = true)String sn){
        return productNewService.unbundledUsedStatus(sn);
    }
    
    @GetMapping("/page")
    public R getPage(@RequestParam("offset") Long offset, @RequestParam("size") Long size, DeliverQuery deliver) {
        return R.ok(deliverService.getPage(offset, size, deliver));
    }
    /**
     * 获取发货详情信息
     * @param sn
     * @return
     */
    @GetMapping("/info")
    public R getDeliverInfo(@RequestParam(value = "sn", required = true)String sn) {
        return R.ok(deliverService.getDeliverInfo(sn));
    }


}
