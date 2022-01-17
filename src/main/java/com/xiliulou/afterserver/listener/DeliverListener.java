package com.xiliulou.afterserver.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
    private HttpServletRequest request;

    public DeliverListener(DeliverService deliverService, CustomerService customerService, SupplierService supplierService,HttpServletRequest request) {
        this.deliverService = deliverService;
        this.supplierService = supplierService;
        this.customerService = customerService;
        this.request = request;
    }

    @Override
    public void invoke(DeliverInfo deliverInfo, AnalysisContext analysisContext) {
        log.info("发货导入=====解析到一条数据:{}", JSON.toJSONString(deliverInfo));
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

            deliver.setCity(item.getStartPoint());
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
            deliver.setState(item.getStatus());
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


}
