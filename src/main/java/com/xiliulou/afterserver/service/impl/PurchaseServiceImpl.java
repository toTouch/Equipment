package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.Purchase;
import com.xiliulou.afterserver.mapper.PurchaseMapper;
import com.xiliulou.afterserver.service.PurchaseService;
import com.xiliulou.afterserver.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 19:09
 **/
@Slf4j
@Service
public class PurchaseServiceImpl extends ServiceImpl<PurchaseMapper, Purchase> implements PurchaseService {


    @Override
    public IPage getPage(Long offset, Long size, Purchase purchase) {
        Page page = PageUtil.getPage(offset, size);

        return baseMapper.selectPage(page, Wrappers.lambdaQuery(purchase).orderByDesc(Purchase::getCreateTime));
    }


}
