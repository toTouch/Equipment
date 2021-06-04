package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.entity.WorkOrder;
import com.xiliulou.afterserver.exception.BadRequestException;
import com.xiliulou.afterserver.service.DeliverService;
import com.xiliulou.afterserver.service.WorkOrderService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ProductSerialNumberQuery;
import com.xiliulou.afterserver.web.query.WorkOrderQuery;
import com.xiliulou.afterserver.web.query.WorkerOrderUpdateStatusQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;

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
    public R insert(@Validated @RequestBody WorkOrderQuery workOrder,HttpServletRequest request) {

        Long uid = (Long) request.getAttribute("uid");
        if (Objects.isNull(uid)){
            return R.fail("请传入uid");
        }
        workOrder.setProcessor(uid.toString());
        workOrder.setCreaterId(uid);

        return workOrderService.saveWorkerOrder(workOrder);
    }

    @PutMapping("/update/workorder/status")
    public R updateWorkOrderStatus(@RequestBody WorkerOrderUpdateStatusQuery query,HttpServletRequest request){
        return workOrderService.updateWorkOrderStatus(query,request);
    }

    @PutMapping("admin/workOrder")
    public R update(@Validated @RequestBody WorkOrder workOrder) {
        return R.ok(workOrderService.updateById(workOrder));
    }

    @DeleteMapping("admin/workOrder")
    public R delete(@Validated @RequestParam("id") Long id) {
        return R.ok(workOrderService.removeById(id));
    }


    @GetMapping("admin/workOrder/exportExcel")
    public void exportExcel(WorkOrderQuery workOrder, HttpServletResponse response) {
        workOrderService.exportExcel(workOrder, response);
    }
//
//    @Deprecated
//    @GetMapping("admin/workOrder/reconciliationSummary")
//    public R reconciliationSummary(WorkOrderQuery workOrder) {
//        return workOrderService.reconciliationSummary(workOrder);
//    }
//
//    @Deprecated
//    @GetMapping("admin/workOrder/reconciliationPreview")
//    public R reconciliationPreview(WorkOrderQuery workOrder) {
//        return workOrderService.reconciliationPreview(workOrder);
//    }

    @GetMapping("admin/workOrder/reconciliation")
    public R reconciliation(WorkOrderQuery workOrder) {
        return workOrderService.reconciliation(workOrder);
    }

    @GetMapping("admin/workOrder/reconciliation/exportExcel")
    public void reconciliationExportExcel(WorkOrderQuery workOrder, HttpServletResponse response) {
        workOrderService.reconciliationExportExcel(workOrder, response);
    }

    @GetMapping("admin/workOrder/staffFuzzy")
    public R staffFuzzy(@RequestParam("accessToken") String accessToken ){
       List<WorkOrder> workOrders = workOrderService.staffFuzzy(accessToken);
       return R.ok(workOrders);
    }

    @PostMapping("admin/workOrder/serialNumber")
    public R insertSerialNumber(@RequestBody ProductSerialNumberQuery productSerialNumberQuery) {

        return workOrderService.insertSerialNumber(productSerialNumberQuery);
    }

    @GetMapping("admin/workOrder/serialNumber/page")
    public R getSerialNumberPage(@RequestParam("offset") Long offset, @RequestParam("size") Long size,
                                 ProductSerialNumberQuery productSerialNumber) {
        return R.ok(workOrderService.getSerialNumberPage(offset, size, productSerialNumber));
    }

    @PostMapping("admin/workOrder/bindSerialNumber")
    public R pointBindSerialNumber(@RequestBody WorkOrderQuery workOrderQuery) {
        return workOrderService.pointBindSerialNumber(workOrderQuery);
    }
}
