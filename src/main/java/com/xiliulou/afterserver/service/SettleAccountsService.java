package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.PointBindSettleAccounts;
import com.xiliulou.afterserver.entity.SettleAccounts;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.SaveSettleAccountsQuery;
import com.xiliulou.afterserver.web.query.SettleAccountsQuery;

import javax.servlet.http.HttpServletResponse;

public interface SettleAccountsService extends IService<SettleAccounts> {
    Page getPage(Long offset, Long size, SettleAccountsQuery settleAccounts);

    R savePointBindSettleAccountsQuery(SaveSettleAccountsQuery settleAccounts);

    R savePointBindSettleAccounts(PointBindSettleAccounts pointBindSettleAccounts);

    R deletePointBindSettleAccounts(Long id);

    R getPointBindSettleAccountsList(Long id);

    void exportExcel(SettleAccounts settleAccounts, HttpServletResponse response);
}
