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
                                       @RequestParam("status")Integer status){

        return workOrderService.queryAssignmentStatusList(offset, size, status);
    }


    @PutMapping("/admin/wechat/workOrder")
    public R updateAssignment(@RequestBody WorkOrderAssignmentQuery workOrderAssignmentQuery){
        return workOrderService.updateAssignment(workOrderAssignmentQuery);
    }

    @PutMapping("/admin/wechat/workOrder/assignment/status")
    public R updateWorkOrderStatus(@RequestBody WorkerOrderUpdateStatusQuery query, HttpServletRequest request){
        return workOrderService.updateWorkOrderStatus(query);
    }

}
