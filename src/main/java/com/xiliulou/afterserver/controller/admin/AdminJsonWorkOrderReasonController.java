package com.xiliulou.afterserver.controller.admin;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiliulou.afterserver.constant.cache.WorkOrderConstant;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.entity.WorkOrderReason;
import com.xiliulou.afterserver.service.WorkOrderReasonService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.cache.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Wrapper;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 10:50
 **/
@RestController
@Slf4j
public class AdminJsonWorkOrderReasonController {

    @Autowired
    WorkOrderReasonService workOrderReasonService;
    @Autowired
    private RedisService redisService;

    @GetMapping("admin/workOrderReason/all")
    public R getPage() {
        return R.ok(workOrderReasonService.list());
    }

    @GetMapping("/admin/reason/tree/list")
    public R getTreeList(){
        return workOrderReasonService.getTreeList();
    }

    @PostMapping("admin/workOrderReason")
    public R insert(@RequestBody WorkOrderReason workOrderReason) {

        return R.ok(workOrderReasonService.save(workOrderReason));
    }

    @PutMapping("admin/workOrderReason")
    public R update(@RequestBody WorkOrderReason workOrderReason) {
        redisService.delete(WorkOrderConstant.WORK_ORDER_REASON + workOrderReason.getId());
        return R.ok(workOrderReasonService.updateById(workOrderReason));
    }

    @DeleteMapping("admin/workOrderReason")
    public R delete(@RequestParam("id") Long id) {
//        workOrderReasonService.remove(Wrappers.<WorkOrderReason>lambdaUpdate().eq(WorkOrderReason::getParentId, id));
//      Integer  del  =  workOrderReasonService.deleteById(id);
//      if (del > 0){
//          return R.ok();
//      }
        return R.ok(workOrderReasonService.deleteById(id));
//        return R.fail("删除错误");
    }
}
