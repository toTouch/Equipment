package com.xiliulou.afterserver.controller.factory;

import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.service.DeliverService;
import com.xiliulou.afterserver.service.ProductNewService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.web.query.DeliverFactoryQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * @author zgw
 * @date 2022/2/25 11:47
 * @mood
 */
@RestController
@RequestMapping("/admin/factory")
public class JsonAdminOrderDeliverController {
    @Autowired
    DeliverService deliverService;
    @Autowired
    ProductNewService productNewService;

    /**
     * 订单发货列表
     * @param offset
     * @param size
     * @return
     */
    @GetMapping("/deliver/list")
    public R queryListByFactory(@RequestParam(value = "offset",required = false)Long offset,
                                @RequestParam(value = "size", required = false)Long size){

        if(!Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_FACTORY)){
            return R.fail("登陆用户非工厂类型");
        }

        if(Objects.isNull(size)){
            size = 50L;
        }

        if(Objects.isNull(offset)){
            offset = 0L;
        }

        //分页 手持终端第一页从零开始
        offset = offset  * size;
        return deliverService.queryListByFactory(offset, size);
    }

    /**
     * 发货内容
     * @param no
     * @return
     */
    @GetMapping("/deliver/content")
    public R queryContentByFactory(@RequestParam("no")String no){

        if(!Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_FACTORY)){
            return R.fail("登陆用户非工厂类型");
        }

        return deliverService.queryContentByFactory(no);
    }

    /**
     * 获取柜机信息
     * @param no
     * @return
     */
    @PostMapping("productNew/check/property")
    public R checkProperty(@RequestParam("no") String no, @RequestParam("deliverNo")String deliverNo){
        return productNewService.checkProperty(no, deliverNo);
    }

    /**
     * 发货
     * @param deliverFactoryQuery
     * @return
     */
    @PostMapping("/deliver")
    public R factoryDeliver(@RequestBody DeliverFactoryQuery deliverFactoryQuery){
        return deliverService.factoryDeliver(deliverFactoryQuery);
    }

    /**
     * 发货记录，还没用
     * @return
     */
    @GetMapping("/deliver/issue/list")
    public R queryIssueListByFactory(){
        if(!Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_FACTORY)){
            return R.fail("登陆用户非工厂类型");
        }
        return deliverService.queryIssueListByFactory();
    }

    @GetMapping("/deliver/issue/info")
    public R queryIssueInfo(@RequestParam("no") String no){
        return deliverService.queryIssueInfo(no);
    }
}
