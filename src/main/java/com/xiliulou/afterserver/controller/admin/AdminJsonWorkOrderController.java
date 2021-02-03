package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.entity.Product;
import com.xiliulou.afterserver.entity.WorkOrder;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.service.WorkOrderService;
import com.xiliulou.afterserver.service.WorkOrderTypeService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.SaveWorkOrderQuery;
import com.xiliulou.afterserver.web.query.WorkOrderQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-01 09:25
 **/
@RestController
@Slf4j
public class AdminJsonWorkOrderController {

    @Autowired
    WorkOrderService workOrderService;


    @GetMapping("admin/workOrder/page")
    public R getPage(@RequestParam("offset") Long offset, @RequestParam("size") Long size, WorkOrderQuery workOrder) {
        return R.ok(workOrderService.getPage(offset, size, workOrder));
    }


    @PostMapping("admin/workOrder")
    public R insert(@RequestBody WorkOrderQuery workOrder, HttpServletRequest request) {

        Long uid = (Long) request.getAttribute("uid");
        workOrder.setCreaterId(uid);
        return workOrderService.insertWorkOrder(workOrder);
    }

    @PutMapping("admin/workOrder")
    public R update(@RequestBody WorkOrder workOrder) {
        return R.ok(workOrderService.updateById(workOrder));
    }

    @DeleteMapping("admin/workOrder")
    public R delete(@RequestParam("id") Long id) {
        return R.ok();
    }


    @GetMapping("admin/workOrder/exportExcel")
    public void exportExcel(WorkOrderQuery workOrder, HttpServletResponse response) {
        workOrderService.exportExcel(workOrder, response);
    }
}
