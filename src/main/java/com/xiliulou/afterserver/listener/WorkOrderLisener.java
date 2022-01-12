package com.xiliulou.afterserver.listener;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.export.DeliverInfo;
import com.xiliulou.afterserver.export.WorkOrderInfo;
import com.xiliulou.afterserver.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.security.Provider;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Hardy
 * @date 2021/10/19 10:59
 * @mood
 */
@Slf4j
public class WorkOrderLisener extends AnalysisEventListener<WorkOrderInfo> {

    private static final int BATCH_COUNT = 2000;
    List<WorkOrderInfo> list = new ArrayList<>();

    private PointNewService pointNewService;
    private CustomerService customerService;
    private ServerService serverService;
    private WorkOrderService workOrderService;
    private SupplierService supplierService;

    public WorkOrderLisener(PointNewService pointNewService,
                            CustomerService customerService,
                            ServerService serverService,
                             WorkOrderService workOrderService,
                            SupplierService supplierService){

        this.pointNewService = pointNewService;
        this.customerService = customerService;
        this.serverService = serverService;
        this.workOrderService = workOrderService;
        this.supplierService = supplierService;
    }


    @Override
    public void invoke(WorkOrderInfo workOrderInfo, AnalysisContext analysisContext) {
        log.info("工单导入=====解析到一条数据:{}", JSON.toJSONString(workOrderInfo));
        if(Objects.isNull(workOrderInfo.getStatus())){
            throw new RuntimeException("status is required the current value is empty!");
        }
        list.add(workOrderInfo);
        if (list.size() >= BATCH_COUNT) {
            saveData();
            list.clear();
        }
    }

    private void saveData() {
        log.info("{}条数据，开始存储数据库！", list.size());

        List<WorkOrder> workOrderList = new ArrayList<>();
        this.list.forEach(item -> {
            WorkOrder workOrder = new WorkOrder();
            //BeanUtils.copyProperties(item, workOrder);

            PointNew pointNew = pointNewService.getOne(new QueryWrapper<PointNew>().eq("name",item.getPointName()).eq("del_flag", PointNew.DEL_NORMAL));
            if(Objects.nonNull(pointNew)){
                workOrder.setPointId(pointNew.getId());
            }

            if(Objects.nonNull(item.getFee())){
                workOrder.setFee(new BigDecimal(item.getFee()));
            }

            /**
             * 第三方公司
             */
            if(Objects.nonNull(item.getThirdCompanyType())){
                Integer thirdCompanyType = this.getThirdCompanyType(item.getThirdCompanyType());
                if(Objects.nonNull(thirdCompanyType)){
                    workOrder.setThirdCompanyType(thirdCompanyType);
                    if(thirdCompanyType == 1){
                        Customer customer = customerService.getOne(new QueryWrapper<Customer>().eq("name", item.getThirdCompanyName()));
                        if(Objects.nonNull(customer)){
                            workOrder.setThirdCompanyName(customer.getName());
                            workOrder.setThirdCompanyId(customer.getId());
                        }
                    }
                    if(thirdCompanyType == 2){
                        Supplier supplier = supplierService.getOne(new QueryWrapper<Supplier>().eq("name", item.getThirdCompanyName()));
                        if(Objects.nonNull(supplier)){
                            workOrder.setThirdCompanyName(supplier.getName());
                            workOrder.setThirdCompanyId(supplier.getId());
                        }
                    }
                    if(thirdCompanyType == 3){
                        Server server = serverService.getOne(new QueryWrapper<Server>().eq("name", item.getThirdCompanyName()));
                        if(Objects.nonNull(server)){
                            workOrder.setThirdCompanyName(server.getName());
                            workOrder.setThirdCompanyId(server.getId());
                        }
                    }
                }
            }



            workOrder.setThirdResponsiblePerson(item.getThirdResponsiblePerson());

            Server server = serverService.getOne(new QueryWrapper<Server>().eq("name", item.getServerName()));
            if(Objects.nonNull(server)){
                workOrder.setServerName(server.getName());
                workOrder.setServerId(server.getId());
            }

            if(Objects.nonNull(item.getProcessTime())){
                long processTime = dateToStamp(item.getProcessTime());
                if(processTime != 0){
                    workOrder.setProcessTime(processTime);
                }
            }


            workOrder.setOrderNo(RandomUtil.randomString(10));

            workOrder.setCreaterId(8L);


            if(Objects.nonNull(item.getDescribeinfo())){
                workOrder.setDescribeinfo(item.getDescribeinfo());
            }

            if(Objects.nonNull(item.getPaymentMethod())){
                if("月结".equals(item.getPaymentMethod()) || String.valueOf(WorkOrder.PAYMENT_METHOD_MONTHLY).equals(item.getPaymentMethod())){
                    workOrder.setPaymentMethod(WorkOrder.PAYMENT_METHOD_MONTHLY);
                }
                if("现结".equals(item.getPaymentMethod()) || String.valueOf(WorkOrder.PAYMENT_METHOD_NOW).equals(item.getPaymentMethod())){
                    workOrder.setPaymentMethod(WorkOrder.PAYMENT_METHOD_NOW);
                }
            }

            if(Objects.nonNull(item.getThirdPaymentStatus())){
                if("无需结算".equals(item.getThirdPaymentStatus()) || String.valueOf(WorkOrder.THIRD_PAYMENT_UNWANTED).equals(item.getThirdPaymentStatus())){
                    workOrder.setThirdPaymentStatus(WorkOrder.THIRD_PAYMENT_UNWANTED);
                }
                if("未结算".equals(item.getThirdPaymentStatus()) || String.valueOf(WorkOrder.THIRD_PAYMENT_UNFINISHED).equals(item.getThirdPaymentStatus())){
                    workOrder.setThirdPaymentStatus(WorkOrder.THIRD_PAYMENT_UNFINISHED);
                }
                if("已结算".equals(item.getThirdPaymentStatus()) || String.valueOf(WorkOrder.THIRD_PAYMENT_FINISHED).equals(item.getThirdPaymentStatus())){
                    workOrder.setThirdPaymentStatus(WorkOrder.THIRD_PAYMENT_FINISHED);
                }
            }

            workOrder.setCreateTime(System.currentTimeMillis());

            if(Objects.nonNull(item.getType())){
                workOrder.setType(item.getType() + "");
            }

            if(Objects.nonNull(item.getThirdCompanyPay())){
                workOrder.setThirdCompanyPay(new BigDecimal(item.getThirdCompanyPay()));
            }

            if(Objects.nonNull(item.getWorkOrderReasonId())){
                workOrder.setWorkOrderReasonId(Long.valueOf(item.getWorkOrderReasonId()));
            }

            if(Objects.nonNull(item.getStatus())){
                workOrder.setStatus(item.getStatus());
            }

            if(Objects.nonNull(item.getInfo())){
                workOrder.setInfo(item.getInfo());
            }

            workOrderList.add(workOrder);
        });
        workOrderService.saveBatch(workOrderList);
        log.info("存储数据库成功！");
    }


    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();
        log.info("工单导入=====所有数据解析完成！");
    }


    private long dateToStamp(String s){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
            long ts = date.getTime();
            return ts;
        } catch (ParseException e) {
            e.printStackTrace();
            log.error("工单导入：转换时间戳异常！dateTime={}",s);
            return 0;
        }
    }

    private Integer getThirdCompanyType(String thirdCompanyType){
        Integer thirdCompanyTypeNum = null;
        if("1".equals(thirdCompanyType) || "客户".equals(thirdCompanyType)){
            thirdCompanyTypeNum = 1;
        }
        if("2".equals(thirdCompanyType) || "供应商".equals(thirdCompanyType)){
            thirdCompanyTypeNum = 2;
        }
        if("3".equals(thirdCompanyType) || "服务商".equals(thirdCompanyType)){
            thirdCompanyTypeNum = 3;
        }
        return thirdCompanyTypeNum;
    }
}
