package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.InventoryFlowBill;
import com.xiliulou.afterserver.web.query.InventoryFlowBillQuery;

/**
 * @author Hardy
 * @date 2021/9/18 13:55
 * @mood
 */
public interface InventoryFlowBillService  extends IService<InventoryFlowBill> {

    IPage getPage(Long offset, Long size, InventoryFlowBillQuery inventoryFlowBillQuery);
}
