package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.entity.PointBindSettleAccounts;
import com.xiliulou.afterserver.entity.SettleAccounts;
import com.xiliulou.afterserver.service.SettleAccountsService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.SaveSettleAccountsQuery;
import com.xiliulou.afterserver.web.query.SettleAccountsQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

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
    public R getPage(@RequestParam("offset") Long offset, @RequestParam("size") Long size, SettleAccountsQuery settleAccounts) {
        return R.ok(settleAccountsService.getPage(offset, size, settleAccounts));
    }


    @PostMapping("admin/settleAccounts")
    public R insert(@RequestBody SettleAccounts settleAccounts) {
        settleAccounts.setCreateTime(System.currentTimeMillis());
        return R.ok(settleAccountsService.save(settleAccounts));
    }

    @Deprecated
    @PostMapping("admin/settleAccounts/bindPoint")
    public R insert(@RequestBody SaveSettleAccountsQuery saveSettleAccountsQuery) {

        return R.ok(settleAccountsService.savePointBindSettleAccountsQuery(saveSettleAccountsQuery));
    }


    @PostMapping("admin/settleAccounts/bindPoint/v2")
    public R savePointBindSettleAccounts(@RequestBody PointBindSettleAccounts pointBindSettleAccounts) {

        return settleAccountsService.savePointBindSettleAccounts(pointBindSettleAccounts);
    }

    @DeleteMapping("admin/settleAccounts/bindPoint")
    public R deletePointBindSettleAccounts(@RequestParam("id") Long id) {

        return settleAccountsService.deletePointBindSettleAccounts(id);
    }

    @GetMapping("admin/settleAccounts/bindPoint/list/{id}")
    public R getPointBindSettleAccountsList(@PathVariable("id") Long id) {

        return settleAccountsService.getPointBindSettleAccountsList(id);
    }

    @PutMapping("admin/settleAccounts")
    public R update(@RequestBody SettleAccounts settleAccounts) {
        return R.ok(settleAccountsService.updateById(settleAccounts));
    }

    @DeleteMapping("admin/settleAccounts")
    public R delete(@RequestParam("id") Long id) {
        return R.ok();
    }

    @GetMapping("admin/settleAccounts/exportExcel")
    public void exportExcel(SettleAccounts settleAccounts, HttpServletResponse response) {
        settleAccountsService.exportExcel(settleAccounts, response);
    }
}
