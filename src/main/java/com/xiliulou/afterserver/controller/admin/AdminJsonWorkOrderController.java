package com.xiliulou.afterserver.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.xiliulou.afterserver.entity.WorkOrder;
import com.xiliulou.afterserver.exception.BadRequestException;
import com.xiliulou.afterserver.export.PointInfo;
import com.xiliulou.afterserver.export.WorkOrderInfo;
import com.xiliulou.afterserver.listener.PointListener;
import com.xiliulou.afterserver.listener.WorkOrderLisener;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.web.query.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    @Autowired
    PointNewService pointNewService;

    @Autowired
    CustomerService customerService;

    @Autowired
    ServerService serverService;

    @Autowired
    SupplierService supplierService;
    @Autowired
    WorkOrderTypeService workOrderTypeService;
    @Autowired
    UserService userService;
    @Autowired
    WarehouseService warehouseService;


    @GetMapping("admin/workOrder/page")
    public R getPage(@RequestParam("offset") Long offset, @RequestParam("size") Long size, WorkOrderQuery workOrder) {
        return R.ok(workOrderService.getPage(offset, size, workOrder));
    }


    @PostMapping("admin/workOrder")
    public R insert(@Validated @RequestBody WorkOrderQuery workOrder,HttpServletRequest request) {

        Long uid = SecurityUtils.getUid();
        if (Objects.isNull(uid)){
            return R.fail("请传入uid");
        }
        workOrder.setProcessor(uid.toString());
        workOrder.setCreaterId(uid);
        workOrder.setCreateTime(System.currentTimeMillis());
        workOrder.setAuditStatus(WorkOrder.AUDIT_STATUS_WAIT);
        return workOrderService.saveWorkerOrder(workOrder);
    }

    @PutMapping("admin/update/workorder/status")
    public R updateWorkOrderStatus(@RequestBody WorkerOrderUpdateStatusQuery query,HttpServletRequest request){
//        Long uid = (Long) request.getAttribute("uid");
//        if (Objects.isNull(uid)){
//            return R.fail("请传入uid");
//        }
//        query.setUid(uid);
        return workOrderService.updateWorkOrderStatus(query);
    }

    @PutMapping("admin/update/workorder/processTime/{id}/{serverId}")
    public R updateWorkorderProcessTime(@PathVariable("id") Long id, @PathVariable("serverId") Long serverId){
        return workOrderService.updateWorkorderProcessTime(id, serverId);
    }

    @PutMapping("admin/workOrder")
    public R update(@Validated @RequestBody WorkOrderQuery workOrder) {
        return workOrderService.updateWorkOrder(workOrder);
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

    @PostMapping("admin/workOrder/upload")
    public R update(MultipartFile file){
        ExcelReader excelReader = null;
        try {
            excelReader = EasyExcel.read(file.getInputStream(), WorkOrderInfo.class, new WorkOrderLisener(pointNewService, customerService,  serverService,  workOrderService, supplierService,workOrderTypeService,userService,warehouseService)).build();
        } catch (Exception e) {
            log.error("workOrder upload error!", e);
            if (e.getCause() instanceof ExcelDataConvertException) {
                ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException) e.getCause();
                String cellMsg = "";
                CellData cellData = excelDataConvertException.getCellData();
                //这里有一个celldatatype的枚举值,用来判断CellData的数据类型
                CellDataTypeEnum type = cellData.getType();
                if (type.equals(CellDataTypeEnum.NUMBER)) {
                    cellMsg = cellData.getNumberValue().toString();
                } else if (type.equals(CellDataTypeEnum.STRING)) {
                    cellMsg = cellData.getStringValue();
                } else if (type.equals(CellDataTypeEnum.BOOLEAN)) {
                    cellMsg = cellData.getBooleanValue().toString();
                }
                String errorMsg = String.format("excel表格:第%s行,第%s列,数据值为:%s,该数据值不符合要求,请检验后重新导入!<span style=\"color:red\">请检查其他的记录是否有同类型的错误!</span>", excelDataConvertException.getRowIndex() + 1, excelDataConvertException.getColumnIndex(), cellMsg);
                log.error(errorMsg);
            }
        }
        ReadSheet readSheet = EasyExcel.readSheet(0).build();
        excelReader.read(readSheet);
        excelReader.finish();
        return R.ok();
    }

    @PostMapping("admin/workOrder/update/auditStatus")
    public R updateAuditStatus(@RequestBody WorkOrderAuditStatusQuery workOrderAuditStatusQuery) {
        return workOrderService.updateAuditStatus(workOrderAuditStatusQuery);
    }

    @PutMapping("admin/workOrder/update/createUser")
    public R putAdminPointNewCreateUser(@RequestParam("id") Long id, @RequestParam("createrId") Long createrId){
        return workOrderService.putAdminPointNewCreateUser(id, createrId);
    }
}
