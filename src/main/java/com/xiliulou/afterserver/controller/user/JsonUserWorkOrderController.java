package com.xiliulou.afterserver.controller.user;

import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.entity.WorkOrder;
import com.xiliulou.afterserver.service.WorkOrderService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.web.query.WorkOrderAssignmentQuery;
import com.xiliulou.afterserver.web.query.WorkerOrderUpdateStatusQuery;
import org.bouncycastle.jcajce.provider.util.SecretKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
                                       @RequestParam(value = "createTimeStart", required = false) Long createTimeStart,
                                       @RequestParam(value = "status", required = false)Integer status){

        return workOrderService.queryAssignmentStatusList(offset, size,auditStatus, status, type, createTimeStart);
    }


    @PutMapping("/admin/wechat/assignment/workOrder")
    public R updateAssignment(@RequestBody WorkOrderAssignmentQuery workOrderAssignmentQuery){
        if(Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_COMMISSIONER)){
            return R.fail("请使用专员账号进行登录");
        }
        return workOrderService.updateAssignment(workOrderAssignmentQuery);
    }

    @PutMapping("/admin/wechat/server/workOrder")
    public R updateServer(@RequestParam("solution") String solution, @RequestParam("workOrderId") Long workOrderId){
        if(Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_PATROL_APPLET)){
            return R.fail("请使用服务商账号进行登录");
        }
        return workOrderService.updateServer(solution, workOrderId);
    }

    @PutMapping("/admin/wechat/workOrder/assignment/status")
    public R updateWorkOrderStatus(@RequestBody WorkerOrderUpdateStatusQuery query, HttpServletRequest request){
        if(Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_COMMISSIONER)){
            return R.fail("请使用专员账号进行登录");
        }
        return workOrderService.updateWorkOrderStatus(query);
    }

    @PutMapping("/admin/wechat/workOrder/submit/review")
    public R submitReview(@RequestParam("id") Long id, @RequestParam("status")Integer status){
        return workOrderService.submitReview(id, status);
    }
}
