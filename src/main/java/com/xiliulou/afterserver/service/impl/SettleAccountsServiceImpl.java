package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
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
import com.xiliulou.afterserver.web.vo.PointBindSettleAccountsVo;
import com.xiliulou.afterserver.web.vo.SettleAccountsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
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
        baseMapper.settleAccountsPage(page, settleAccounts);
        List<SettleAccountsVo> settleAccountsVoList = page.getRecords();
        if (ObjectUtil.isNotEmpty(settleAccountsVoList)) {

            for (SettleAccountsVo settleAccountsVo : settleAccountsVoList) {
                if (ObjectUtil.isEmpty(settleAccountsVo.getPointBindSettleAccountsList())) {

                    settleAccountsVo.setUnBindPointAmount(settleAccountsVo.getTotalAmount());

                } else {
                    BigDecimal totalAmount = settleAccountsVo.getTotalAmount();

                    List<PointBindSettleAccountsVo> pointBindSettleAccountsVoList = settleAccountsVo.getPointBindSettleAccountsList();

                    for (PointBindSettleAccountsVo pointBindSettleAccountsVo : pointBindSettleAccountsVoList) {
                        totalAmount = totalAmount.subtract(pointBindSettleAccountsVo.getDeviceAmount()).subtract(pointBindSettleAccountsVo.getServerAmount());
                    }
                    settleAccountsVo.setUnBindPointAmount(totalAmount);
                }

            }
            page.setRecords(settleAccountsVoList);
        }


        return page;
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
        BigDecimal allAmount = settleAccountsDb.getTotalAmount();
        BigDecimal bindPointAllAmount = BigDecimal.ZERO;
        for (PointBindSettleAccountsQuery pointBindSettleAccountsQuery : settleAccounts.getPointBindSettleAccountsQueryList()) {


            if (Objects.nonNull(pointBindSettleAccountsQuery.getId())) {

                PointBindSettleAccounts pointBindSettleAccounts = pointBindSettleAccountsMapper.selectById(pointBindSettleAccountsQuery.getId());
                if (Objects.isNull(pointBindSettleAccounts)) {
                    log.error("SAVE_POINT_BIND_SETTLE_ACCOUNTS_QUERY ERROR , NOT FOUND pointBindSettleAccounts BY ID:{}", pointBindSettleAccountsQuery.getId());
                    throw new CustomBusinessException("未找到绑定点位结算记录!");
                }
                Point point = pointService.getById(pointBindSettleAccounts.getPointId());
                if (Objects.isNull(point)) {
                    log.error("SAVE_POINT_BIND_SETTLE_ACCOUNTS_QUERY ERROR , NOT FOUND POINT BY ID:{}", pointBindSettleAccountsQuery.getPointId());
                    throw new CustomBusinessException("未找到点位!");
                }
                BigDecimal
                        deviceAmount = Objects.isNull(pointBindSettleAccountsQuery.getDeviceAmount()) ? BigDecimal.ZERO : pointBindSettleAccountsQuery.getDeviceAmount();
                BigDecimal serverAmount = Objects.isNull(pointBindSettleAccountsQuery.getServerAmount()) ? BigDecimal.ZERO : pointBindSettleAccountsQuery.getServerAmount();
                bindPointAllAmount = bindPointAllAmount.add(deviceAmount).add(serverAmount);
                //修改
                BigDecimal paiedAmount = Objects.isNull(point.getPaiedAmount()) ? BigDecimal.ZERO : point.getPaiedAmount();

                paiedAmount = paiedAmount.subtract(pointBindSettleAccounts.getDeviceAmount()).add(deviceAmount);
                paiedAmount = paiedAmount.subtract(pointBindSettleAccounts.getServerAmount()).add(serverAmount);
                point.setPaiedAmount(paiedAmount);
                pointService.updateById(point);
                PointBindSettleAccounts pointBindSettleAccountsUpdate = new PointBindSettleAccounts();

            } else {
                Point point = pointService.getById(pointBindSettleAccountsQuery.getPointId());
                if (Objects.isNull(point)) {
                    log.error("SAVE_POINT_BIND_SETTLE_ACCOUNTS_QUERY ERROR , NOT FOUND POINT BY ID:{}", pointBindSettleAccountsQuery.getPointId());
                    throw new CustomBusinessException("未找到点位!");
                }
                BigDecimal
                        deviceAmount = Objects.isNull(pointBindSettleAccountsQuery.getDeviceAmount()) ? BigDecimal.ZERO : pointBindSettleAccountsQuery.getDeviceAmount();
                BigDecimal serverAmount = Objects.isNull(pointBindSettleAccountsQuery.getServerAmount()) ? BigDecimal.ZERO : pointBindSettleAccountsQuery.getServerAmount();
                bindPointAllAmount = bindPointAllAmount.add(deviceAmount).add(serverAmount);
                PointBindSettleAccounts pointBindSettleAccounts = new PointBindSettleAccounts();
                pointBindSettleAccounts.setSettleAccountsId(settleAccounts.getSettleAccountsId());
                pointBindSettleAccounts.setPointId(pointBindSettleAccountsQuery.getPointId());
                pointBindSettleAccounts.setDeviceAmount(deviceAmount);
                pointBindSettleAccounts.setServerAmount(serverAmount);
                pointBindSettleAccounts.setCreateTime(System.currentTimeMillis());
                pointBindSettleAccountsMapper.insert(pointBindSettleAccounts);
                if (Objects.nonNull(pointBindSettleAccounts.getDeviceAmount())) {

                    point.setPaiedAmount(point.getDeviceAmount().add(pointBindSettleAccounts.getDeviceAmount()));
                }
                if (Objects.nonNull(pointBindSettleAccounts.getServerAmount())) {
                    point.setServerAmount(point.getServerAmount().add(pointBindSettleAccountsQuery.getServerAmount()));
                }
                pointService.updateById(point);
            }


        }
        if (bindPointAllAmount.compareTo(allAmount) > 0) {
            log.error("save_Point_Bind_SettleAccounts error ,bind_Point_All_Amount greater than All_Amount,bindPointAllAmount:{},allAmount:{}", bindPointAllAmount, allAmount);
            throw new CustomBusinessException("绑定点位总金额不能大于付款金额!");
        }

        return R.ok();
    }

    /**
     * 保存绑定点位对账记录
     *
     * @param pointBindSettleAccounts
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R savePointBindSettleAccounts(PointBindSettleAccounts pointBindSettleAccounts) {
        Point point = pointService.getById(pointBindSettleAccounts.getPointId());
        if (Objects.isNull(point)) {
            log.error("SAVE_POINT_BIND_SETTLE_ACCOUNTS ERROR , NOT FOUND POINT BY ID:{}", pointBindSettleAccounts.getPointId());
            throw new CustomBusinessException("未找到点位!");
        }
        SettleAccounts settleAccounts = baseMapper.selectById(pointBindSettleAccounts.getSettleAccountsId());
        if (Objects.isNull(settleAccounts)) {
            return R.failMsg("未找到结算记录!");
        }


        List<PointBindSettleAccounts> pointBindSettleAccountsList = pointBindSettleAccountsMapper.selectList(
                Wrappers.<PointBindSettleAccounts>lambdaQuery()
                        .eq(PointBindSettleAccounts::getSettleAccountsId, pointBindSettleAccounts.getSettleAccountsId()));
        BigDecimal AllAmount = settleAccounts.getTotalAmount();
        BigDecimal compareAmount = pointBindSettleAccounts.getDeviceAmount().
                add(pointBindSettleAccounts.getServerAmount());
        if (ObjectUtil.isNotEmpty(pointBindSettleAccountsList)) {
            for (PointBindSettleAccounts ps : pointBindSettleAccountsList) {
                compareAmount = compareAmount.add(ps.getDeviceAmount()).add(ps.getServerAmount());
            }
        }
        if (compareAmount.compareTo(AllAmount) > 0) {
            return R.failMsg("绑定点位总金额不能大于付款金额!");
        }
        pointBindSettleAccounts.setCreateTime(System.currentTimeMillis());
        pointBindSettleAccountsMapper.insert(pointBindSettleAccounts);
        if (Objects.nonNull(pointBindSettleAccounts.getDeviceAmount())) {

            point.setPaiedAmount(point.getPaiedAmount().add(pointBindSettleAccounts.getDeviceAmount()));
        }
        if (Objects.nonNull(pointBindSettleAccounts.getServerAmount())) {
            point.setServerAmount(point.getPaiedAmount().add(pointBindSettleAccounts.getServerAmount()));
        }
        pointService.updateById(point);
        return R.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R deletePointBindSettleAccounts(Long id) {
        PointBindSettleAccounts pointBindSettleAccounts = pointBindSettleAccountsMapper.selectById(id);
        if (Objects.isNull(pointBindSettleAccounts)) {
            return R.failMsg("未找到绑定点位记录!");
        }

        SettleAccounts settleAccounts = baseMapper.selectById(pointBindSettleAccounts.getSettleAccountsId());
        if (Objects.isNull(settleAccounts)) {
            return R.failMsg("未找到结算记录!");
        }
        Point point = pointService.getById(pointBindSettleAccounts.getPointId());
        if (Objects.isNull(point)) {
            log.error("SAVE_POINT_BIND_SETTLE_ACCOUNTS ERROR , NOT FOUND POINT BY ID:{}", pointBindSettleAccounts.getPointId());
            throw new CustomBusinessException("未找到点位!");
        }

        if (Objects.nonNull(pointBindSettleAccounts.getDeviceAmount())) {

            point.setPaiedAmount(point.getPaiedAmount().subtract(pointBindSettleAccounts.getDeviceAmount()));
        }
        if (Objects.nonNull(pointBindSettleAccounts.getServerAmount())) {
            point.setServerAmount(point.getPaiedAmount().subtract(pointBindSettleAccounts.getServerAmount()));
        }
        pointService.updateById(point);
        pointBindSettleAccountsMapper.deleteById(id);
        return R.ok();
    }

    @Override
    public R getPointBindSettleAccountsList(Long id) {

        return R.ok(pointBindSettleAccountsMapper.getPointBindSettleAccountsList(id));
    }

    //导出excel
    @Override
    public void exportExcel(SettleAccounts settleAccounts, HttpServletResponse response) {
        List<SettleAccounts> settleAccountsList = list();
        if (ObjectUtil.isEmpty(settleAccountsList)) {
            throw new CustomBusinessException("没有查询到产品型号!无法导出！");
        }
        List<SettleAccountsVo> settleAccountsVos = new ArrayList<>(settleAccountsList.size());
        for (SettleAccounts s : settleAccountsList) {
            SettleAccountsVo settleAccountsVo = new SettleAccountsVo();
            BeanUtil.copyProperties(s, settleAccountsVo);
            settleAccountsVos.add(settleAccountsVo);
        }

        String fileName = "产品型号.xlsx";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            EasyExcel.write(outputStream, SettleAccountsVo.class).sheet("sheet").doWrite(settleAccountsVos);
            return;
        } catch (IOException e) {
            log.error("导出报表失败！", e);
        }
        throw new CustomBusinessException("导出报表失败！请联系客服！");
    }
}
