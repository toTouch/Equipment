package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.entity.Product;
import com.xiliulou.afterserver.entity.WorkOrderType;
import com.xiliulou.afterserver.service.WorkOrderTypeService;
import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 10:43
 **/
@RestController
@Slf4j
public class AdminJsonWorkOrderTypeController {


    @Autowired
    WorkOrderTypeService workOrderTypeService;


    @GetMapping("admin/workOrderType/all")
    public R getPage() {
        return R.ok(workOrderTypeService.list());
    }


    @PostMapping("admin/workOrderType")
    public R insert(@RequestBody WorkOrderType workOrderType) {

        return R.ok(workOrderTypeService.save(workOrderType));
    }

    @PutMapping("admin/workOrderType")
    public R update(@RequestBody WorkOrderType workOrderType) {
        return R.ok(workOrderTypeService.updateById(workOrderType));
    }

    @DeleteMapping("admin/workOrderType")
    public R delete(@RequestParam("id") Long id) {
        return R.ok();
    }


}
