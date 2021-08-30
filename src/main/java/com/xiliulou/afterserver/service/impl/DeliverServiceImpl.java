package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.Deliver;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.exception.CustomBusinessException;
import com.xiliulou.afterserver.mapper.DeliverMapper;
import com.xiliulou.afterserver.service.DeliverService;
import com.xiliulou.afterserver.service.UserService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.vo.DeliverExcelVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 19:05
 **/
@Service
@Slf4j
public class DeliverServiceImpl extends ServiceImpl<DeliverMapper, Deliver> implements DeliverService {

    @Autowired
    private UserService userService;

    @Override
    public IPage getPage(Long offset, Long size, Deliver deliver) {

        Page page = PageUtil.getPage(offset, size);
        Page selectPage = baseMapper.selectPage(page,
                new LambdaQueryWrapper<Deliver>()
                        .like(Objects.nonNull(deliver.getCreateUid()),Deliver::getCreateUid,deliver.getCreateUid())
                        .like(Objects.nonNull(deliver.getExpressNo()),Deliver::getExpressNo,deliver.getExpressNo())
                        .like(Objects.nonNull(deliver.getExpressCompany()),Deliver::getExpressCompany,deliver.getExpressCompany())
                        .orderByDesc(Deliver::getCreateTime)
                        .like(Objects.nonNull(deliver.getCity()),Deliver::getCity,deliver.getCity())
                        .like(Objects.nonNull(deliver.getDestination()),Deliver::getDestination,deliver.getDestination()));
        List<Deliver> list = (List<Deliver>) selectPage.getRecords();
        if (list.isEmpty()){
            return selectPage;
        }

        list.forEach(records -> {
            if (Objects.nonNull(records.getCreateUid())){
                User userById = userService.getUserById(records.getCreateUid());
                if (Objects.nonNull(userById)){
                    records.setUserName(userById.getUserName());
                }
               records.setUserName(userById.getUserName());
            }
        });

        return selectPage.setRecords(list);
    }

    //导出excel
    @Override
    public void exportExcel(Deliver deliver, HttpServletResponse response) {
        List<Deliver> deliverList = list();

        if (ObjectUtil.isEmpty(deliverList)) {
            throw new CustomBusinessException("没有查询到产品型号!无法导出！");
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<DeliverExcelVo> deliverExcelVoList = new ArrayList<>(deliverList.size());
        for (Deliver d : deliverList) {
            DeliverExcelVo deliverExcelVo = new DeliverExcelVo();
            BeanUtil.copyProperties(d, deliverExcelVo);
            deliverExcelVo.setExpressNo(getExpressNo(d.getState()));
            deliverExcelVo.setCreateTime(simpleDateFormat.format(new Date(d.getCreateTime())));
            if (ObjectUtil.isNotEmpty(d.getDeliverTime())) {
                deliverExcelVo.setDeliverTime(simpleDateFormat.format(new Date(d.getDeliverTime())));
            }
            deliverExcelVoList.add(deliverExcelVo);
        }

        String fileName = "发货管理.xlsx";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            EasyExcel.write(outputStream, DeliverExcelVo.class).sheet("sheet").doWrite(deliverExcelVoList);
            return;
        } catch (IOException e) {
            log.error("导出报表失败！", e);
        }
        throw new CustomBusinessException("导出报表失败！请联系客服！");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R updateStatusFromBatch(List<Long> ids, Integer status) {
        int row = this.baseMapper.updateStatusFromBatch(ids,status);
        if(row == 0){
            return R.fail("未修改数据");
        }
        return R.ok();
    }

    private String getExpressNo(Integer status) {
        String statusStr = "";
        switch (status) {
            case 1:
                statusStr = "待处理";
                break;
            case 2:
                statusStr = "处理中";
                break;
            case 3:
                statusStr = "已完成";
                break;
        }
        return statusStr;
    }
}
