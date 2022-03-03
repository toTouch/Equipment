package com.xiliulou.afterserver.listener;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.export.DeliverInfo;
import com.xiliulou.afterserver.export.WorkOrderInfo;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.web.query.WorkOrderQuery;
import com.xiliulou.core.json.JsonUtil;
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
    private WorkOrderTypeService workOrderTypeService;
    private UserService userService;
    private WarehouseService warehouseService;

    public WorkOrderLisener(PointNewService pointNewService,
                            CustomerService customerService,
                            ServerService serverService,
                             WorkOrderService workOrderService,
                            SupplierService supplierService,
                            WorkOrderTypeService workOrderTypeService,
                            UserService userService,
                            WarehouseService warehouseService){

        this.pointNewService = pointNewService;
        this.customerService = customerService;
        this.serverService = serverService;
        this.workOrderService = workOrderService;
        this.supplierService = supplierService;
        this.workOrderTypeService = workOrderTypeService;
        this.userService = userService;
        this.warehouseService = warehouseService;
    }


    @Override
    public void invoke(WorkOrderInfo workOrderInfo, AnalysisContext analysisContext) {
        log.info("工单导入=====解析到一条数据:{}", JSON.toJSONString(workOrderInfo));
        checkProperties(workOrderInfo);
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

            workOrder.setOverInsurance(getOverInsurance(item.getOverInsurance()));

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

            workOrder.setCreaterId(SecurityUtils.getUid());


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
            Integer type = getWorkOrderType(item.getType());
            if(Objects.nonNull(item.getType())){
                workOrder.setType(type + "");
            }

            if(Objects.nonNull(item.getThirdCompanyPay())){
                workOrder.setThirdCompanyPay(new BigDecimal(item.getThirdCompanyPay()));
            }

            if(Objects.nonNull(item.getWorkOrderReasonId())){
                workOrder.setWorkOrderReasonId(Long.valueOf(item.getWorkOrderReasonId()));
            }

            if(Objects.nonNull(item.getStatus())){
                workOrder.setStatus(this.getStatus(item.getStatus()));
            }

            if(Objects.nonNull(item.getInfo())){
                workOrder.setInfo(item.getInfo());
            }



            if(Objects.equals(type, WorkOrder.TYPE_MOBLIE)){
                Integer sourceType = getSourceType(item.getSourceType());
                workOrder.setSourceType(sourceType);
                if(Objects.equals(sourceType, 1)){
                    QueryWrapper<PointNew> wrapper = new QueryWrapper<>();
                    wrapper.eq("name", item.getTransferSourcePoint());
                    wrapper.eq("del_flag", WorkOrder.DEL_NORMAL);
                    PointNew p = pointNewService.getOne(wrapper);
                    workOrder.setTransferSourcePointId(p.getId());
                }

                if(Objects.equals(sourceType, 2)){
                    QueryWrapper<WareHouse> wrapper = new QueryWrapper<>();
                    wrapper.eq("ware_houses", item.getTransferSourcePoint());
                    WareHouse wareHouse = warehouseService.getOne(wrapper);
                    workOrder.setTransferSourcePointId(new Long(wareHouse.getId()));
                }


                Integer destinationType = getDestinationType(item.getDestinationType());

                if(Objects.equals(destinationType, 1)){
                    QueryWrapper<PointNew> wrapper = new QueryWrapper<>();
                    wrapper.eq("name", item.getTransferDestinationPoint());
                    wrapper.eq("del_flag", WorkOrder.DEL_NORMAL);
                    PointNew p = pointNewService.getOne(wrapper);
                    workOrder.setTransferDestinationPointId(p.getId());
                }

                if(Objects.equals(destinationType, 2)){
                    QueryWrapper<WareHouse> wrapper = new QueryWrapper<>();
                    wrapper.eq("ware_houses", item.getTransferDestinationPoint());
                    WareHouse wareHouse = warehouseService.getOne(wrapper);
                    workOrder.setTransferDestinationPointId(new Long(wareHouse.getId()));
                }

            }

            workOrder.setAuditStatus(WorkOrder.AUDIT_STATUS_WAIT);
            workOrder.setThirdReason(item.getThirdReason());
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

    private Integer getOverInsurance(String overInsurance){
        Integer overInsuranceInt = null;
        if("0".equals(overInsurance) || "未过保".equals(overInsurance)){
            overInsuranceInt = 0;
        }
        if("1".equals(overInsurance) || "已过保".equals(overInsurance)){
            overInsuranceInt = 1;
        }
        return overInsuranceInt;
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

    private Integer getStatus(String status) {
        if("1".equals(status) || "待处理".equals(status)){
            return 1;
        }
        if("2".equals(status) || "已处理".equals(status)){
            return 2;
        }
        if("3".equals(status) || "待分析".equals(status)){
            return 3;
        }
        if("4".equals(status) || "已完结".equals(status)){
            return 4;
        }
        if("5".equals(status) || "已暂停".equals(status)){
            return 5;
        }
        return null;
    }

    private Integer getWorkOrderType(String type){
        if("1".equals(type) || "安装".equals(type)){
            return 1;
        }
        if("2".equals(type) || "派送".equals(type)){
            return 2;
        }
        if("3".equals(type) || "施工".equals(type)){
            return 3;
        }
        if("4".equals(type) || "移机".equals(type)){
            return 4;
        }
        if("5".equals(type) || "维护".equals(type)){
            return 5;
        }
        if("6".equals(type) || "归仓".equals(type)){
            return 6;
        }
        if("7".equals(type) || "派送安装".equals(type)){
            return 7;
        }
        return null;
    }

    private Integer getSourceType(String sourceType){
        if("1".equals(sourceType) || "点位".equals(sourceType)){
            return 1;
        }
        if("2".equals(sourceType) || "仓库".equals(sourceType)){
            return 2;
        }
        return null;
    }

    private Integer getDestinationType(String destinationType){
        if("1".equals(destinationType) || "点位".equals(destinationType)){
            return 1;
        }
        if("2".equals(destinationType) || "仓库".equals(destinationType)){
            return 2;
        }
        return null;
    }


    private void checkProperties(WorkOrderInfo workOrderInfo){
        Integer type = getWorkOrderType(workOrderInfo.getType());
        if(Objects.isNull(type)){
            throw new RuntimeException("请填写工单类型!");
        }else{
            WorkOrderType workOrderType = workOrderTypeService.getById(type);
            if (Objects.isNull(workOrderType)){
                throw new RuntimeException("请填写正确的工单类型");
            }
        }

        if(Objects.isNull(workOrderInfo.getPointName())){
            throw new RuntimeException("请填写点位");
        }else{
            PointNew pointNew = pointNewService.getOne(new QueryWrapper<PointNew>().eq("name",workOrderInfo.getPointName()).eq("del_flag", PointNew.DEL_NORMAL));
            if(Objects.isNull(pointNew)){
                throw new RuntimeException("未查询到相关点位");
            }

        }

        Long uid = SecurityUtils.getUid();
        if(Objects.isNull(uid)){
            throw new RuntimeException("创建人为空");
        }else{
            User user = userService.getUserById(uid);
            if (Objects.isNull(user)) {
                throw new RuntimeException("未查询到相关创建人");
            }
        }

        Integer status = this.getStatus(workOrderInfo.getStatus());
        if(Objects.isNull(status)){
            throw new RuntimeException("请填写工单状态");
        }

        if(Objects.isNull(workOrderInfo.getProcessTime())){
            throw new RuntimeException("请填写处理时间");
        }

        if(Objects.equals(type, WorkOrder.TYPE_MOBLIE)){
            Integer sourceType = getSourceType(workOrderInfo.getSourceType());
            if(Objects.isNull(sourceType)){
                throw new RuntimeException("请填写起点类型");
            }

            if(StringUtils.isBlank(workOrderInfo.getTransferSourcePoint())){
                throw new RuntimeException("请填写起点");
            }else{
                if(Objects.equals(sourceType, 1)){
                    QueryWrapper<PointNew> wrapper = new QueryWrapper<>();
                    wrapper.eq("name", workOrderInfo.getTransferSourcePoint());
                    wrapper.eq("del_flag", WorkOrder.DEL_NORMAL);
                    PointNew pointNew = pointNewService.getOne(wrapper);
                    if(Objects.isNull(pointNew)){
                        throw new RuntimeException("未查询到相关起点【"+ workOrderInfo.getTransferSourcePoint() +"】");
                    }
                }

                if(Objects.equals(sourceType, 2)){
                    QueryWrapper<WareHouse> wrapper = new QueryWrapper<>();
                    wrapper.eq("ware_houses", workOrderInfo.getTransferSourcePoint());
                    WareHouse wareHouse = warehouseService.getOne(wrapper);
                    if(Objects.isNull(wareHouse)){
                        throw new RuntimeException("未查询到相关起点【"+ workOrderInfo.getTransferSourcePoint() +"】");
                    }
                }
            }

            Integer destinationType = getDestinationType(workOrderInfo.getDestinationType());
            if(Objects.isNull(destinationType)){
                throw new RuntimeException("请填写终点类型");
            }
            if(StringUtils.isBlank(workOrderInfo.getTransferDestinationPoint())){
                throw new RuntimeException("请填写终点");
            }else{
                if(Objects.equals(destinationType, 1)){
                    QueryWrapper<PointNew> wrapper = new QueryWrapper<>();
                    wrapper.eq("name", workOrderInfo.getTransferDestinationPoint());
                    wrapper.eq("del_flag", WorkOrder.DEL_NORMAL);
                    PointNew pointNew = pointNewService.getOne(wrapper);
                    if(Objects.isNull(pointNew)){
                        throw new RuntimeException("未查询到相关起点【"+ workOrderInfo.getTransferDestinationPoint() +"】");
                    }
                }

                if(Objects.equals(destinationType, 2)){
                    QueryWrapper<WareHouse> wrapper = new QueryWrapper<>();
                    wrapper.eq("ware_houses", workOrderInfo.getTransferDestinationPoint());
                    WareHouse wareHouse = warehouseService.getOne(wrapper);
                    if(Objects.isNull(wareHouse)){
                        throw new RuntimeException("未查询到相关起点【"+ workOrderInfo.getTransferDestinationPoint() +"】");
                    }
                }
            }
        }
        return;
    }
}
