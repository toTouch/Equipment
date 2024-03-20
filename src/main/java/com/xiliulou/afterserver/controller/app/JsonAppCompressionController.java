package com.xiliulou.afterserver.controller.app;

import com.xiliulou.afterserver.entity.FactoryTestConf;
import com.xiliulou.afterserver.plugin.MyLog;
import com.xiliulou.afterserver.service.FactoryTestConfService;
import com.xiliulou.afterserver.service.ProductNewService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ApiRequestQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zgw
 * @date 2022/2/25 15:23
 * @mood
 */
@RestController
@RequestMapping("/app/compression")
public class JsonAppCompressionController {

    @Autowired
    private ProductNewService productNewService;

    @Autowired
    private FactoryTestConfService factoryTestConfService;

    @PostMapping("/check")
    @MyLog
    public R checkCompression(@RequestBody ApiRequestQuery apiRequestQuery){
        return productNewService.checkCompression(apiRequestQuery);
    }
    
    @PostMapping("/getIotTypeAndCheck")
    public R queryCabinetIotType(@RequestBody ApiRequestQuery apiRequestQuery){
        return productNewService.queryCabinetIotType(apiRequestQuery);
    }

    @PostMapping("/loadTest")
    @MyLog
    public R loadTest(@RequestBody ApiRequestQuery apiRequestQuery){
        return productNewService.runFullLoadTest(apiRequestQuery);
    }

    @PostMapping("/success")
    @MyLog
    public R successCompression(@RequestBody ApiRequestQuery apiRequestQuery){
        return productNewService.successCompression(apiRequestQuery);
    }

    /**
     * 压测2.0，压测结果单笔改成多笔 人机交互
     */
    @PostMapping("/loadTest2")
    @MyLog
    public R loadTest2(@RequestBody ApiRequestQuery apiRequestQuery){
        return productNewService.runFullLoadTest2(apiRequestQuery);
    }

    /**
     * 配置列表`
     */
    @PostMapping("/listConf")
    @MyLog
    public List<FactoryTestConf> listConf(@RequestBody ApiRequestQuery apiRequestQuery){
        return factoryTestConfService.getAllByApi(apiRequestQuery);
    }


}
