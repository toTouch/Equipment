package com.xiliulou.afterserver.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiliulou.afterserver.controller.BaseController;
import com.xiliulou.afterserver.entity.Customer;
import com.xiliulou.afterserver.entity.PointNew;
import com.xiliulou.afterserver.export.CustomerInfo;
import com.xiliulou.afterserver.listener.ClientListener;
import com.xiliulou.afterserver.mapper.PointNewMapper;
import com.xiliulou.afterserver.service.CustomerService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.vo.PointNewInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.Wrapper;
import java.util.List;
import java.util.Objects;

/**
 * @program: XILIULOU
 * @description:  客户列表
 * @author: Mr.YG
 * @create: 2021-01-29 09:00
 **/
@RestController
@Slf4j
public class AdminJsonCustomerController extends BaseController {
    @Autowired
    CustomerService customerService;


    @PostMapping("admin/customer")
    public R insert(@RequestBody Customer customer) {
        BaseMapper<Customer> baseMapper = customerService.getBaseMapper();
        Customer customerOld = baseMapper.selectOne(new QueryWrapper<Customer>().eq("name", customer.getName()));
        if(Objects.nonNull(customerOld)){
            return R.fail("客户列表中已存在【"+ customer.getName() + "】");
        }
        customer.setCreateTime(System.currentTimeMillis());
        return R.ok(customerService.save(customer));
    }


    @GetMapping("admin/customer/page")
    public R page(@RequestParam("offset") Long offset, @RequestParam("size") Long size, Customer customer) {

        return R.ok(customerService.getCustomerPage(offset, size, customer));

    }
    
    /**
     * 提供给前端 获取客户和产品型号和数量
     *
     * @param customer
     * @return
     */
    @GetMapping("admin/customer/customerList")
    public R list(Customer customer) {
        return customerService.getCustomerList(customer);
    }

    @PutMapping("admin/customer")
    public R update(@RequestBody Customer customer) {
        BaseMapper<Customer> baseMapper = customerService.getBaseMapper();
        Customer customerOld = baseMapper.selectOne(new QueryWrapper<Customer>().eq("name", customer.getName()));
        if(Objects.nonNull(customerOld) && !Objects.equals(customer.getId(), customerOld.getId())){
            return R.fail("客户列表中已存在【"+ customer.getName() + "】");
        }

        return R.ok(customerService.updateById(customer));
    }


    @DeleteMapping("admin/customer")
    public R delete(@RequestParam("id") Long id, @RequestParam(value = "falg", required = false)Long falg) {
        return customerService.delete(id, falg);
    }

    @GetMapping("admin/customer/list")
    public R list() {
        return R.ok(customerService.list(Wrappers.<Customer>lambdaQuery().orderByDesc(Customer::getCreateTime)));
    }

    /**
     * 导入
     */
    @PostMapping("admin/customer/upload")
    public R upload(MultipartFile file) throws IOException {

        ExcelReader excelReader = EasyExcel.read(file.getInputStream(), CustomerInfo.class,new ClientListener(customerService)).build();
        ReadSheet readSheet = EasyExcel.readSheet(0).build();
        excelReader.read(readSheet);
        excelReader.finish();
        return R.ok();
    }

    @GetMapping("admin/customer/search")
    public R customerSearch(@RequestParam("offset")Long offset, @RequestParam("size")Long size,
        @RequestParam(value = "name", required = false)String name){
        if(Objects.isNull(offset) || offset < 0) {
            offset = 0L;
        }
        if(Objects.isNull(size) || size < 0 || size > 20) {
            size = 20L;
        }
        return customerService.customerSearch(offset, size, name);
    }

}
