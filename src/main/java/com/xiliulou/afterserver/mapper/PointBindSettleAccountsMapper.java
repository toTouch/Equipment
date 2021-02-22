package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.PointBindSettleAccounts;
import com.xiliulou.afterserver.web.vo.PointBindSettleAccountsVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PointBindSettleAccountsMapper extends BaseMapper<PointBindSettleAccounts> {

    List<PointBindSettleAccountsVo> getPointBindSettleAccountsList(@Param("id") Long id);
}
