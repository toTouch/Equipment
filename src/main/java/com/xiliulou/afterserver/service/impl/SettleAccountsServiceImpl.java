package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.SettleAccounts;
import com.xiliulou.afterserver.mapper.SettleAccountsMapper;
import com.xiliulou.afterserver.service.SettleAccountsService;
import com.xiliulou.afterserver.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 18:13
 **/
@Service
@Slf4j
public class SettleAccountsServiceImpl extends ServiceImpl<SettleAccountsMapper, SettleAccounts> implements SettleAccountsService {

    @Override
    public Page getPage(Long offset, Long size, SettleAccounts settleAccounts) {
        Page page = PageUtil.getPage(offset, size);
        return baseMapper.selectPage(page, Wrappers.lambdaQuery(settleAccounts));
    }
}
