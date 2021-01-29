package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.entity.Product;
import com.xiliulou.afterserver.entity.SettleAccounts;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.service.SettleAccountsService;
import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 18:14
 **/
@RestController
@Slf4j
public class AdminJsonSettleAccountsController {

    @Autowired
    SettleAccountsService settleAccountsService;


    @GetMapping("admin/settleAccounts/page")
    public R getPage(@RequestParam("offset") Long offset, @RequestParam("size") Long size, SettleAccounts settleAccounts) {
        return R.ok(settleAccountsService.getPage(offset, size, settleAccounts));
    }


    @PostMapping("admin/settleAccounts")
    public R insert(@RequestBody SettleAccounts settleAccounts) {
        settleAccounts.setCreateTime(System.currentTimeMillis());
        return R.ok(settleAccountsService.save(settleAccounts));
    }

    @PutMapping("admin/settleAccounts")
    public R update(@RequestBody SettleAccounts settleAccounts) {
        return R.ok(settleAccountsService.updateById(settleAccounts));
    }

    @DeleteMapping("admin/settleAccounts")
    public R delete(@RequestParam("id") Long id) {
        return R.ok();
    }


}
