package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.WorkOrder;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ProductSerialNumberQuery;
import com.xiliulou.afterserver.web.query.SaveWorkOrderQuery;
import com.xiliulou.afterserver.web.query.WorkOrderQuery;
import com.xiliulou.afterserver.web.vo.WorkOrderVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface WorkOrderService extends IService<WorkOrder> {
    IPage getPage(Long offset, Long size, WorkOrderQuery workOrder);

    @Deprecated
    R saveWorkOrder(SaveWorkOrderQuery saveWorkOrderQuery);

    void exportExcel(WorkOrderQuery workOrder, HttpServletResponse response);

    R insertWorkOrder(WorkOrderQuery workOrder);
//
//    R reconciliationSummary(WorkOrderQuery workOrder);
//
//    R reconciliationPreview(WorkOrderQuery workOrder);

    R reconciliation(WorkOrderQuery workOrder);

    void reconciliationExportExcel(WorkOrderQuery workOrder, HttpServletResponse response);

     List<WorkOrderVo> getWorkOrderList(WorkOrderQuery workOrder);

    List<WorkOrder> staffFuzzy(String accessToken);

    R insertSerialNumber(ProductSerialNumberQuery productSerialNumberQuery);

    IPage getSerialNumberPage(Long offset, Long size, ProductSerialNumberQuery productSerialNumber);

    R pointBindSerialNumber(WorkOrderQuery workOrderQuery);
}
