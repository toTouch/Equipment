package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.Point;
import com.xiliulou.afterserver.entity.PointBindSettleAccounts;
import com.xiliulou.afterserver.entity.SettleAccounts;
import com.xiliulou.afterserver.exception.CustomBusinessException;
import com.xiliulou.afterserver.mapper.PointBindSettleAccountsMapper;
import com.xiliulou.afterserver.mapper.SettleAccountsMapper;
import com.xiliulou.afterserver.service.PointService;
import com.xiliulou.afterserver.service.SettleAccountsService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.PointBindSettleAccountsQuery;
import com.xiliulou.afterserver.web.query.SaveSettleAccountsQuery;
import com.xiliulou.afterserver.web.query.SettleAccountsQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 18:13
 **/
@Service
@Slf4j
public class SettleAccountsServiceImpl extends ServiceImpl<SettleAccountsMapper, SettleAccounts> implements SettleAccountsService {

    @Autowired
    PointBindSettleAccountsMapper pointBindSettleAccountsMapper;
    @Autowired
    PointService pointService;

    @Override
    public Page getPage(Long offset, Long size, SettleAccountsQuery settleAccounts) {
        Page page = PageUtil.getPage(offset, size);
        return baseMapper.settleAccountsPage(page, settleAccounts);
    }

    /**
     * @param settleAccounts
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R savePointBindSettleAccountsQuery(SaveSettleAccountsQuery settleAccounts) {
        SettleAccounts settleAccountsDb = getById(settleAccounts.getSettleAccountsId());
        if (Objects.isNull(settleAccountsDb)) {
            log.error("SAVE_POINT_BIND_SETTLE_ACCOUNTS_QUERY ERROR ,NOT FOUND SETTLE_ACCOUNTS BY ID:{}", settleAccounts.getSettleAccountsId());
            return R.failMsg("未找到结算记录!");
        }
        for (PointBindSettleAccountsQuery pointBindSettleAccountsQuery : settleAccounts.getPointBindSettleAccountsQueryList()) {

            Point point = pointService.getById(pointBindSettleAccountsQuery.getPointId());
            if (Objects.isNull(point)) {
                log.error("SAVE_POINT_BIND_SETTLE_ACCOUNTS_QUERY ERROR , NOT FOUND POINT BY ID:{}", pointBindSettleAccountsQuery.getPointId());
                throw new CustomBusinessException("未找到点位!");
            }
            PointBindSettleAccounts pointBindSettleAccounts = new PointBindSettleAccounts();
            pointBindSettleAccounts.setSettleAccountsId(settleAccounts.getSettleAccountsId());
            pointBindSettleAccounts.setPointId(pointBindSettleAccountsQuery.getPointId());
            pointBindSettleAccounts.setDeviceAmount(Objects.isNull(pointBindSettleAccountsQuery.getDeviceAmount()) ? BigDecimal.ZERO : pointBindSettleAccountsQuery.getDeviceAmount());
            pointBindSettleAccounts.setServerAmount(Objects.isNull(pointBindSettleAccountsQuery.getServerAmount()) ? BigDecimal.ZERO : pointBindSettleAccountsQuery.getServerAmount());
            pointBindSettleAccounts.setCreateTime(System.currentTimeMillis());
            pointBindSettleAccountsMapper.insert(pointBindSettleAccounts);
        }
        return null;
    }
}
