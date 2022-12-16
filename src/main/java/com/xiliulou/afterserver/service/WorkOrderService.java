package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.WorkOrder;
import com.xiliulou.afterserver.entity.WorkOrderParts;
import com.xiliulou.afterserver.entity.WorkOrderType;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.*;
import com.xiliulou.afterserver.web.vo.AfterCountListVo;
import com.xiliulou.afterserver.web.vo.AfterCountVo;
import com.xiliulou.afterserver.web.vo.AfterOrderVo;
import com.xiliulou.afterserver.web.vo.WorkOrderVo;
import java.math.BigDecimal;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    R saveWorkerOrder(WorkOrderQuery workOrder);

    String generateWorkOrderNo(WorkOrderType type, String no);

    Long queryMaxDaySumNoByType(Long startTime, Long endTime, Long id);

    R updateWorkOrderStatus(WorkerOrderUpdateStatusQuery query);

    R updateWorkOrder(WorkOrderQuery workOrder);

    Boolean checkServerProcess(Long id);

    List<AfterCountVo> qualityCount(Long pointId, Integer cityId,Long startTime,Long endTime);

    List<AfterCountListVo> qualityCountList(Long pointId, Integer cityId, Long startTime,Long endTime);

    List<AfterOrderVo> afterWorkOrderByCity(Long pointId, Integer cityId, Long startTime,Long endTime);

    List<AfterOrderVo> afterWorkOrderByPoint(Long pointId, Integer cityId, Long startTime,Long endTime);

    List<AfterOrderVo> afterWorkOrderList(Long pointId, Integer cityId, Long startTime,Long endTime);

    List<AfterOrderVo> installWorkOrderByCity(Long pointId, Integer cityId, Long startTime,Long endTime);

    List<AfterOrderVo> installWorkOrderByPoint(Long pointId, Integer cityId, Long startTime,Long endTime);

    List<AfterOrderVo> installWorkOrderList(Long pointId, Integer cityId, Long startTime,Long endTime);

    R updateWorkorderProcessTime(Long id, Long serverId);

    R updateAuditStatus(WorkOrderAuditStatusQuery workOrderAuditStatusQuery);

    R putAdminPointNewCreateUser(Long id, Long createrId);

    R queryAssignmentStatusList(Long offset, Long size,Integer auditStatus, Integer status, Integer type, Long createTimeStart, String serverName, String pointName);

    R updateAssignment(WorkOrderAssignmentQuery workOrderAssignmentQuery);

    R updateServer(String solution, Long workOrderId);

    R submitReview(Long id);

    String getParentWorkOrderReason(Long id);

    R queryProductModelPull(String name);

    R queryWorkOrderReasonPull();

    R queryServerPull(String name);

    R querySupplierPull(String name);

    R queryCustomerPull(String name);

    R queryPointNewPull(String name);

    R queryWarehousePull(String name);

    R workOrderServerAuditEntry(Long id);

    boolean checkAndclearEntry(List<WorkOrderParts> workOrderParts);

    BigDecimal clareAndAddWorkOrderParts(Long oid, Long sid, List<WorkOrderParts> WorkOrderParts, Integer type);
}
