package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.Customer;
import com.xiliulou.afterserver.mapper.CustomerMapper;
import com.xiliulou.afterserver.service.CustomerService;
import com.xiliulou.afterserver.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.security.auth.login.CredentialException;
import java.util.Objects;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 19:06
 **/
@Service
@Slf4j
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {


    @Override
    public IPage getCustomerPage(Long offset, Long size, Customer customer) {
        Page page = PageUtil.getPage(offset, size);

        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Objects.nonNull(customer.getName()),Customer::getName,customer.getName())
                .like(Objects.nonNull(customer.getPhone()),Customer::getPhone,customer.getPhone())
                .orderByDesc(Customer::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }
}
