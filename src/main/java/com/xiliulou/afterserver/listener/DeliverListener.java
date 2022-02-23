package com.xiliulou.afterserver.listener;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.export.DeliverInfo;
import com.xiliulou.afterserver.export.PointInfo;
import com.xiliulou.afterserver.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Hardy
 * @date 2021/4/25 0025 13:30
 * @Description: 点位表
 */
@Slf4j
public class DeliverListener extends AnalysisEventListener<DeliverInfo> {

    private static final int BATCH_COUNT = 2000;
    List<DeliverInfo> list = new ArrayList<>();

    private DeliverService deliverService;
    private CustomerService customerService;
    private SupplierService supplierService;
    private PointNewService pointNewService;
    private WarehouseService warehouseService;
    private HttpServletRequest request;

    public DeliverListener(DeliverService deliverService, CustomerService customerService, SupplierService supplierService, PointNewService pointNewService, WarehouseService warehouseService,HttpServletRequest request) {
        this.deliverService = deliverService;
        this.supplierService = supplierService;
        this.customerService = customerService;
        this.pointNewService = pointNewService;
        this.warehouseService = warehouseService;
        this.request = request;
    }

    @Override
    public void invoke(DeliverInfo deliverInfo, AnalysisContext analysisContext) {
        log.info("发货导入=====解析到一条数据:{}", JSON.toJSONString(deliverInfo));
        checkProperties(deliverInfo);
        list.add(deliverInfo);
        if (list.size() >= BATCH_COUNT) {
            saveData();
            list.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();
        log.info("客户表导入=====所有数据解析完成！");
    }


    /**
     * 入库
     */
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", list.size());

        List<Deliver> deliverList = new ArrayList<>();
        this.list.forEach(item -> {
            Deliver deliver = new Deliver();
            deliver.setCityType(this.getPointType(item.getStartPointType()));
            deliver.setCity(item.getStartPoint());
            deliver.setDestinationType(this.getPointType(item.getEndPointType()));
            deliver.setDestination(item.getEndPoint());
            deliver.setRemark(item.getRemarks());
            deliver.setDeliverCost(item.getCost());
            try {
                deliver.setDeliverTime(dateToStamp(item.getDeliverTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            deliver.setCreateTime(System.currentTimeMillis());
            deliver.setExpressCompany(item.getExpressName());
            deliver.setExpressNo(item.getExpressNo());
            deliver.setState(this.getStatus(item.getStatus()));
            deliver.setProduct(item.getProduct());
            deliver.setQuantity(item.getQuantity());
            deliver.setCreateUid((Long) request.getAttribute("uid"));

            if (Objects.nonNull(item.getThirdCompanyId()) && Objects.nonNull(item.getThirdCompanyType())){
                Long id = null;
                if (item.getThirdCompanyType()==1){
                    LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<Customer>().eq(Customer::getName, item.getThirdCompanyId());
                    Customer customer = customerService.getBaseMapper().selectOne(wrapper);
                    if (Objects.nonNull(customer)){
                        id = customer.getId();
                    }
                }
                if (item.getThirdCompanyType()==2){
                    LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<Supplier>().eq(Supplier::getName, item.getThirdCompanyId());
                    Supplier supplier = supplierService.getBaseMapper().selectOne(wrapper);
                    if (Objects.nonNull(supplier)){
                        id = supplier.getId();
                    }
                }
                deliver.setThirdCompanyId(id);
            }

            deliver.setThirdCompanyType(item.getThirdCompanyType());
            deliver.setThirdCompanyPay(item.getThirdCompanyPay());
            deliver.setThirdReason(item.getThirdReason());

            if (item.getDeliverTime() != null){
                long l = 0;
                try {
                    l = dateToStamp(item.getDeliverTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                deliver.setDeliverTime(l);
            }else {
                deliver.setDeliverTime(System.currentTimeMillis());
            }

            if(Objects.nonNull(item.getPaymentMethod())){
                if("月结".equals(item.getPaymentMethod()) || String.valueOf(Deliver.PAYMENT_METHOD_MONTHLY).equals(item.getPaymentMethod())){
                    deliver.setPaymentMethod(Deliver.PAYMENT_METHOD_MONTHLY);
                }
                if("现结".equals(item.getPaymentMethod()) || String.valueOf(Deliver.PAYMENT_METHOD_NOW).equals(item.getPaymentMethod())){
                    deliver.setPaymentMethod(Deliver.PAYMENT_METHOD_NOW);
                }
            }

            deliver.setNo(RandomUtil.randomString(10)+"I");
            deliverList.add(deliver);
        });

        deliverService.saveBatch(deliverList);
        log.info("存储数据库成功！");
    }

    /**
     * 将时间转换为时间戳
     */
    public static long dateToStamp(String s) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        return ts;
    }

    private  Integer getPointType(String type){
        Integer pointType = null;
        if("1".equals(type) || "点位".equals(type)){
            pointType = 1;
        }else if("2".equals(type) || "仓库".equals(type)){
            pointType = 2;
        }else if("3".equals(type) || "工厂".equals(type)){
            pointType = 3;
        }
        return pointType;
    }

    private  String getPointTypeName(Integer type){
        String pointType = "";
        switch (type){
            case 1: pointType = "点位"; break;
            case 2: pointType = "仓库"; break;
            case 3: pointType = "工厂"; break;
        }
        return pointType;
    }

    private Integer getStatus(String status){
        Integer statusName = null;
        if("1".equals(status) || "未发货".equals(status)){
            statusName = 1;
        }else if("2".equals(status) || "已发货".equals(status)){
            statusName = 2;
        }else if("3".equals(status) || "已到达".equals(status)){
            statusName = 3;
        }
        return statusName;
    }

    private void checkProperties(DeliverInfo deliverInfo){
        if(Objects.isNull(deliverInfo.getStartPointType())){
            throw new RuntimeException("起点类型不可为空，请检查");
        }

        Integer startPointType = this.getPointType(deliverInfo.getStartPointType());
        if(Objects.isNull(startPointType)){
            throw new RuntimeException("请输入有效的起点类型，错误数据【"+deliverInfo.getStartPointType()+"】");
        }

        Object startPoint = this.queryPoint(startPointType, deliverInfo.getStartPoint());
        if(Objects.isNull(startPoint)){
            throw new RuntimeException(getPointTypeName(startPointType)+"列表中没有起点"+deliverInfo.getStartPoint());
        }

        Integer endPointType = this.getPointType(deliverInfo.getEndPointType());
        if(Objects.isNull(endPointType)){
            throw new RuntimeException("请输入有效的终点类型，错误数据【"+deliverInfo.getEndPointType()+"】");
        }

        Object endPoint = this.queryPoint(endPointType, deliverInfo.getEndPoint());
        if(Objects.isNull(endPoint)){
            throw new RuntimeException(getPointTypeName(endPointType)+"列表中没有终点"+deliverInfo.getStartPoint());
        }

        if(Objects.isNull(getStatus(deliverInfo.getStatus()))){
            throw new RuntimeException("请填写正确物流状态，错误数据【"+deliverInfo.getStatus()+"】");
        }
    }

    private Object queryPoint(Integer pointType, String pointName){
        Object point = null;
        if(Objects.equals(pointType, 1)){
            QueryWrapper<PointNew> wrapper = new QueryWrapper<>();
            wrapper.eq("name", pointName).eq("del_flag", PointNew.DEL_NORMAL);
            point = pointNewService.getBaseMapper().selectOne(wrapper);
        }

        if(Objects.equals(pointType, 2)){
            QueryWrapper<WareHouse> wrapper = new QueryWrapper<>();
            wrapper.eq("ware_houses", pointName);
            point = warehouseService.getBaseMapper().selectOne(wrapper);
        }

        if(Objects.equals(pointType, 3)){
            QueryWrapper<Supplier> wrapper = new QueryWrapper<>();
            wrapper.eq("name", pointName);
            point = supplierService.getBaseMapper().selectOne(wrapper);
        }
        return point;
    }
}
