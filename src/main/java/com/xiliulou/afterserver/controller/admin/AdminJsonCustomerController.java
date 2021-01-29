package com.xiliulou.afterserver.controller.admin;

import com.sun.org.glassfish.external.statistics.annotations.Reset;
import com.xiliulou.afterserver.controller.BaseController;
import com.xiliulou.afterserver.entity.Customer;
import com.xiliulou.afterserver.service.CustomerService;
import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 09:00
 **/
@RestController
@Slf4j
public class AdminJsonCustomerController extends BaseController {
    @Autowired
    CustomerService customerService;

    @PostMapping("customer")
    public R insert(@RequestBody Customer customer) {
        customer.setCreateTime(System.currentTimeMillis());
        return R.ok(customerService.save(customer));

    }


    @GetMapping("customer/page")
    public R page(@RequestParam("offset") Long offset, @RequestParam("size") Long size, Customer customer) {

        return R.ok(customerService.getCustomerPage(offset, size, customer));

    }

    @PutMapping("customer")
    public R update(@RequestBody Customer customer) {
        return R.ok(customerService.updateById(customer));
    }


    @DeleteMapping("customer")
    public R delete(@RequestParam("id") Long id) {
        // TODO: 2021/1/29 0029  校验是否可以删,是否绑定其他内容
        return R.ok();
    }

}
