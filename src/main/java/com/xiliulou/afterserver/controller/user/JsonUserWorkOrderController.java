package com.xiliulou.afterserver.controller.user;

import com.alibaba.excel.util.CollectionUtils;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.entity.WorkOrder;
import com.xiliulou.afterserver.service.WorkOrderService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.web.query.*;
import feign.Body;
import org.bouncycastle.jcajce.provider.util.SecretKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * @author zgw
 * @date 2022/3/30 11:11
 * @mood
 */
@RestController
public class JsonUserWorkOrderController {

    @Autowired
    WorkOrderService workOrderService;

    @GetMapping("/admin/wechat/workOrder")
    public R queryAssignmentStatusList(@RequestParam(value = "offset", required = false, defaultValue = "0") Long offset,
                                       @RequestParam(value = "size",required = false, defaultValue = "20") Long size,
                                       @RequestParam(value = "auditStatus", required = false) Integer auditStatus,
                                       @RequestParam(value = "type", required = false) Integer type,
                                       @RequestParam(value = "startTime", required = false) Long startTime,
                                       @RequestParam(value = "status", required = false)Integer status,
                                       @RequestParam(value = "serverName", required = false) String serverName,
                                       @RequestParam(value = "pointName", required = false) String pointName){

        return workOrderService.queryAssignmentStatusList(offset, size,auditStatus, status, type, startTime, serverName, pointName);
    }


    @PutMapping("/admin/wechat/assignment/workOrder")
    public R updateAssignment(@RequestBody WorkOrderAssignmentQuery workOrderAssignmentQuery){
        if(!Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_COMMISSIONER)){
            return R.fail("请使用专员账号进行登录");
        }
        return workOrderService.updateAssignment(workOrderAssignmentQuery);
    }

    @PutMapping("/admin/wechat/server/workOrder")
    public R updateServer(@RequestBody ServerWorkOrderQuery serverWorkOrderQuery){
        if(!Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_PATROL_APPLET)){
            return R.fail("请使用服务商账号进行登录");
        }

        String solution = serverWorkOrderQuery.getSolution();
        Long workOrderId = serverWorkOrderQuery.getWorkOrderId();
        return workOrderService.updateServer(solution, workOrderId);
    }

    //@PutMapping("/admin/wechat/workOrder/assignment/status")
    public R updateWorkOrderStatus(@RequestBody WorkerOrderUpdateStatusQuery query, HttpServletRequest request){
        if(!Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_COMMISSIONER)){
            return R.fail("请使用专员账号进行登录");
        }
        return workOrderService.updateWorkOrderStatus(query);
    }

    @PutMapping("/admin/wechat/workOrder/submit/review")
    public R submitReview(@RequestBody WorkOrderSubmitReviewQuery workOrderSubmitReviewQuery){
        Long id = workOrderSubmitReviewQuery.getId();
        return workOrderService.submitReview(id);
    }

    @GetMapping("/admin/wechat/productModel/pull")
    public R queryProductModelPull(@RequestParam(value = "name", required = false) String name){
        return workOrderService.queryProductModelPull(name);
    }

    @GetMapping("/admin/wechat/workOrderReason/pull")
    public R queryWorkOrderReasonPull(){
        return workOrderService.queryWorkOrderReasonPull();
    }

    @GetMapping("/admin/wechat/server/pull")
    public R queryServerPull(@RequestParam(value = "name", required = false) String name){
        return workOrderService.queryServerPull(name);
    }

    @GetMapping("/admin/wechat/supplier/pull")
    public R querySupplierPull(@RequestParam(value = "name", required = false) String name){
        return workOrderService.querySupplierPull(name);
    }

    @GetMapping("/admin/wechat/customer/pull")
    public R queryCustomerPull(@RequestParam(value = "name", required = false) String name){
        return workOrderService.queryCustomerPull(name);
    }

    @GetMapping("/admin/wechat/pointNew/pull")
    public R queryPointNewPull(@RequestParam(value = "name", required = false) String name){
        return workOrderService.queryPointNewPull(name);
    }

    @GetMapping("/admin/wechat/warehouse/pull")
    public R queryWarehousePull(@RequestParam(value = "name", required = false) String name){
        return workOrderService.queryWarehousePull(name);
    }

}
