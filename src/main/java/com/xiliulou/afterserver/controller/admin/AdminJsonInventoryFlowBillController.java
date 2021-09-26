package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.controller.BaseController;
import com.xiliulou.afterserver.service.InventoryFlowBillService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.InventoryFlowBillQuery;
import com.xiliulou.afterserver.web.query.WareHouseProductDetailsQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zgw
 * @date 2021/9/18 14:17
 * @mood
 */
@RestController
@Slf4j
public class AdminJsonInventoryFlowBillController extends BaseController {

    @Autowired
    InventoryFlowBillService inventoryFlowBillService;

    @GetMapping("admin/inventoryFlowBill/page")
    public R getPage(@RequestParam("offset") Long offset, @RequestParam("size") Long size, InventoryFlowBillQuery InventoryFlowBillQuery) {
        return R.ok(inventoryFlowBillService.getPage(offset, size, InventoryFlowBillQuery));
    }

}
