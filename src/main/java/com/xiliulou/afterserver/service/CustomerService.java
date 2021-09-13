package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.Customer;
import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.SelectKey;
import org.springframework.stereotype.Service;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 19:06
 **/
@Service

public interface CustomerService extends IService<Customer> {
    IPage getCustomerPage(Long offset, Long size, Customer customer);
    R delete(Long id, Long falg);
}
