package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.SettleAccounts;
import com.xiliulou.afterserver.web.query.SettleAccountsQuery;

public interface SettleAccountsService extends IService<SettleAccounts> {
    Page getPage(Long offset, Long size, SettleAccountsQuery settleAccounts);
}
