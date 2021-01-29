package com.xiliulou.afterserver.controller.admin;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.entity.WorkOrderReason;
import com.xiliulou.afterserver.service.WorkOrderReasonService;
import com.xiliulou.afterserver.util.R;
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


    @GetMapping("admin/workOrderReason/all")
    public R getPage(@RequestParam("offset") Long offset, @RequestParam("size") Long size, WorkOrderReason workOrderReason) {
        return R.ok(workOrderReasonService.list());
    }


    @PostMapping("admin/workOrderReason")
    public R insert(@RequestBody WorkOrderReason workOrderReason) {

        return R.ok(workOrderReasonService.save(workOrderReason));
    }

    @PutMapping("admin/workOrderReason")
    public R update(@RequestBody WorkOrderReason workOrderReason) {
        return R.ok(workOrderReasonService.updateById(workOrderReason));
    }

    @DeleteMapping("admin/workOrderReason")
    public R delete(@RequestParam("id") Long id) {
        workOrderReasonService.removeById(id);
        workOrderReasonService.remove(Wrappers.<WorkOrderReason>lambdaUpdate().eq(WorkOrderReason::getParentId, id));
        return R.ok();
    }
}
