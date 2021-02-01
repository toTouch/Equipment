package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.Purchase;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.PurchaseQuery;
import com.xiliulou.afterserver.web.query.SavePurchaseQuery;

import javax.servlet.http.HttpServletResponse;

public interface PurchaseService extends IService<Purchase> {
    IPage getPage(Long offset, Long size, PurchaseQuery purchase);

    void exportExcel(PurchaseQuery purchase, HttpServletResponse response);

    R savePurchase(SavePurchaseQuery savePurchaseQuery);
}
