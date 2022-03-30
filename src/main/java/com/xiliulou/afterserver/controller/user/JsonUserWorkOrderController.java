package com.xiliulou.afterserver.controller.user;

import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.entity.WorkOrder;
import com.xiliulou.afterserver.service.WorkOrderService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import org.bouncycastle.jcajce.provider.util.SecretKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping("/admin/wechat/workOrder/assignment")
    public R queryAssignmentStatusList(@RequestParam(value = "offset", required = false, defaultValue = "0") Long offset, @RequestParam(value = "size",required = false, defaultValue = "20") Long size){
        if(!Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_COMMISSIONER)){
            return R.fail("用户非专员，没有权限");
        }
        return workOrderService.queryAssignmentStatusList(offset, size);
    }

}
