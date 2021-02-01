package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.Product;
import com.xiliulou.afterserver.entity.WorkOrder;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.SaveWorkOrderQuery;
import com.xiliulou.afterserver.web.query.WorkOrderQuery;

import javax.servlet.http.HttpServletResponse;

public interface WorkOrderService extends IService<WorkOrder> {
    IPage getPage(Long offset, Long size, WorkOrderQuery workOrder);

    R saveWorkOrder(SaveWorkOrderQuery saveWorkOrderQuery);

    void exportExcel(WorkOrderQuery workOrder, HttpServletResponse response);
}
