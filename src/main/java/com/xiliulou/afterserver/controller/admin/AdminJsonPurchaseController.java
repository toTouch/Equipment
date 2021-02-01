package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.entity.Product;
import com.xiliulou.afterserver.entity.Purchase;
import com.xiliulou.afterserver.service.PurchaseService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.SavePurchaseQuery;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 11:13
 **/
@RestController
@Slf4j
public class AdminJsonPurchaseController {

    @Autowired
    PurchaseService purchaseService;

    @GetMapping("admin/purchase/page")
    public R getPage(@RequestParam("offset") Long offset, @RequestParam("size") Long size, Purchase purchase) {
        return R.ok(purchaseService.getPage(offset, size, purchase));
    }


    @PostMapping("admin/purchase")
    public R insert(@RequestBody SavePurchaseQuery savePurchaseQuery) {

        return purchaseService.savePurchase(savePurchaseQuery);
    }

    @PutMapping("admin/purchase")
    public R update(@RequestBody Purchase purchase) {
        return R.ok(purchaseService.updateById(purchase));
    }

    @DeleteMapping("admin/purchase")
    public R delete(@RequestParam("id") Long id) {
        return R.ok();
    }


    @GetMapping("admin/purchase/exportExcel")
    public void exportExcel(Purchase purchase, HttpServletResponse response) {
        purchaseService.exportExcel(purchase, response);
    }
}
