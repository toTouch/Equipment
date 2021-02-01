package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiliulou.afterserver.entity.SettleAccounts;
import com.xiliulou.afterserver.web.query.SettleAccountsQuery;
import com.xiliulou.afterserver.web.vo.SettleAccountsVo;
import org.apache.ibatis.annotations.Param;

public interface SettleAccountsMapper extends BaseMapper<SettleAccounts> {


    Page<SettleAccountsVo> settleAccountsPage(Page page, @Param("query") SettleAccountsQuery settleAccounts);
}
