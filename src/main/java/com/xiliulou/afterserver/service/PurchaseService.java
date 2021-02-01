package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.Purchase;

import javax.servlet.http.HttpServletResponse;

public interface PurchaseService extends IService<Purchase> {
    IPage getPage(Long offset, Long size, Purchase purchase);

    void exportExcel(Purchase purchase, HttpServletResponse response);
}
