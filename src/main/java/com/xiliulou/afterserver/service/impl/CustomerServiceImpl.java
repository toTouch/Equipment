package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.Customer;
import com.xiliulou.afterserver.entity.PointNew;
import com.xiliulou.afterserver.mapper.CustomerMapper;
import com.xiliulou.afterserver.mapper.PointNewMapper;
import com.xiliulou.afterserver.service.CustomerService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.vo.CustomerPullVo;
import com.xiliulou.afterserver.web.vo.PageSearchVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.CredentialException;
import java.util.List;
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
    @Autowired
    PointNewMapper pointNewMapper;
    @Autowired
    CustomerMapper customerMapper;

    @Override
    public IPage getCustomerPage(Long offset, Long size, Customer customer) {
        
        Page page = PageUtil.getPage(offset, size);
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Objects.nonNull(customer.getName()),Customer::getName,customer.getName())
                .like(Objects.nonNull(customer.getPhone()),Customer::getPhone,customer.getPhone())
                .like(Objects.nonNull(customer.getManager()),Customer::getManager,customer.getManager())
                .orderByDesc(Customer::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public R delete(Long id, Long falg) {
        QueryWrapper<PointNew> wrapper = new QueryWrapper<>();
        wrapper.eq("customer_id", id).eq("del_flag", PointNew.DEL_NORMAL);
        List<PointNew> list = pointNewMapper.selectList(wrapper);
        boolean boo = false;

        if(list == null || list.isEmpty()){
            boo = this.removeById(id);
        }else{
            if(Objects.equals(falg, Long.parseLong("1"))){
                boolean isSuccess = this.removeById(id);
                int len = pointNewMapper.delete(wrapper);
                if(isSuccess && len>0){
                    boo = true;
                }
            }else{
                return R.fail("您想要删除的客户有其他关联，不能删除！");
            }
        }
        if(boo){
            return R.ok();
        }else{
            return R.fail("删除失败");
        }
    }

    @Override
    public List<CustomerPullVo> queryCustomerPull(String name) {
        return baseMapper.queryCustomerPull(name);
    }

    @Override
    public R customerSearch(Long offset, Long size, String name) {
        List<PageSearchVo> listVo = baseMapper.customerSearch(offset, size, name);
        return R.ok(listVo);
    }
    
    @Override
    public Customer queryById(Long customerId) {
        return customerMapper.selectById(customerId);
    }
    
    @Override
    public R getCustomerList(Customer customer) {
        List<Customer> customers = customerMapper.selectByParamer(customer);
        return R.ok(customers);
    }
    
}
