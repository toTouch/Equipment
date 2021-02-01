package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiliulou.afterserver.entity.Purchase;
import com.xiliulou.afterserver.web.vo.PurchaseVo;

import java.util.List;

public interface PurchaseMapper extends BaseMapper<Purchase> {
    IPage getPage(Page page, Purchase purchase);

    List<PurchaseVo> getList(Purchase purchase);
}
