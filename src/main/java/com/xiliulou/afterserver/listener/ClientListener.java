package com.xiliulou.afterserver.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.xiliulou.afterserver.entity.Customer;
import com.xiliulou.afterserver.export.CustomerInfo;
import com.xiliulou.afterserver.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hardy
 * @date 2021/4/25 0025 13:30
 * @Description: 客户表导入
 */
@Slf4j
public class ClientListener extends AnalysisEventListener<CustomerInfo> {

    private static final int BATCH_COUNT = 100;
    List<CustomerInfo> list = new ArrayList<>();

    private CustomerService customerService;
    public ClientListener(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Override
    public void invoke(CustomerInfo customerInfo, AnalysisContext analysisContext) {
        log.info("客户表导入=====解析到一条数据:{}", JSON.toJSONString(customerInfo));
        list.add(customerInfo);
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
        List<Customer> customerList = new ArrayList<>();
        list.forEach(item ->{
            Customer customer = new Customer();
            BeanUtils.copyProperties(item, customer);
            customer.setCreateTime(System.currentTimeMillis());
            customerList.add(customer);
        });

        customerService.saveBatch(customerList);
        log.info("存储数据库成功！");
    }







}
