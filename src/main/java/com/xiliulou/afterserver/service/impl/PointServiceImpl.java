package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.File;
import com.xiliulou.afterserver.entity.Point;
import com.xiliulou.afterserver.entity.ProductSerialNumber;
import com.xiliulou.afterserver.exception.CustomBusinessException;
import com.xiliulou.afterserver.mapper.PointBindProductMapper;
import com.xiliulou.afterserver.mapper.PointMapper;
import com.xiliulou.afterserver.mapper.ProductSerialNumberMapper;
import com.xiliulou.afterserver.service.PointService;
import com.xiliulou.afterserver.service.WorkOrderService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.IndexDataQuery;
import com.xiliulou.afterserver.web.query.PointQuery;
import com.xiliulou.afterserver.web.query.WorkOrderQuery;
import com.xiliulou.afterserver.web.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 19:04
 **/
@Service
@Slf4j
public class PointServiceImpl extends ServiceImpl<PointMapper, Point> implements PointService {

    @Autowired
    FileServiceImpl fileService;
    @Autowired
    PointBindProductMapper pointBindProductMapper;
    @Autowired
    ProductSerialNumberMapper productSerialNumberMapper;
    @Autowired
    WorkOrderService workOrderService;

    @Override
    public IPage getPage(Long offset, Long size, PointQuery point) {
        Page page = PageUtil.getPage(offset, size);
        baseMapper.getPointIdList(page, point);
        List<Long> pointIdList = page.getRecords();
        List<PointVo> pointVoList = new ArrayList<>();
        log.info("pointIdList:{}", pointIdList);
        if (ObjectUtil.isNotEmpty(pointIdList)) {
            for (Long id : pointIdList) {
                PointVo pointVo = baseMapper.getPointBaseInfo(id);
                WorkOrderQuery workOrderQuery = new WorkOrderQuery();
                workOrderQuery.setPointId(pointVo.getId());



                List<WorkOrderVo> workOrderVoList = workOrderService.getWorkOrderList(workOrderQuery);
                if (ObjectUtil.isNotEmpty(workOrderVoList)) {
                    pointVo.setWorkOrderVoList(workOrderVoList);
                }
                pointVoList.add(pointVo);

            }
        }
        page.setRecords(pointVoList);
//        baseMapper.pointPage(page, point);
//        if (ObjectUtil.isNotEmpty(page.getRecords())) {
//            List<PointVo> pointVoList = page.getRecords();
//            for (PointVo pointVo : pointVoList) {
//
//                WorkOrderQuery workOrderQuery = new WorkOrderQuery();
//                workOrderQuery.setPointId(point.getId());
//                List<WorkOrderVo> workOrderVoList = workOrderService.getWorkOrderList(workOrderQuery);
//                if (ObjectUtil.isNotEmpty(workOrderVoList)) {
//                    pointVo.setWorkOrderVoList(workOrderVoList);
//                }
//            }
//            page.setRecords(pointVoList);
//        }
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R savePoint(PointQuery pointQuery) {
        Point point = new Point();
        BeanUtils.copyProperties(pointQuery, point);
        baseMapper.insert(point);

        if (ObjectUtil.isNotEmpty(pointQuery.getFileList())) {
            List<File> filList = new ArrayList();
            for (File file : pointQuery.getFileList()) {
                file.setFileType(File.FILE_TYPE_SPOT);
                file.setBindId(point.getId());
                file.setType(File.TYPE_POINT);
                file.setCreateTime(System.currentTimeMillis());
                filList.add(file);
            }
            fileService.saveBatch(filList);
        }
        if (ObjectUtil.isNotEmpty(pointQuery.getProductSerialNumberIdAndSetNoMap())) {
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (Map.Entry<Long, Integer> entry : pointQuery.getProductSerialNumberIdAndSetNoMap().entrySet()) {
                ProductSerialNumber productSerialNumber = productSerialNumberMapper.selectById(entry.getKey());
                if (Objects.isNull(productSerialNumber)) {
                    log.error("not found productSerialNumber by id:{}", entry.getKey());
                    throw new CustomBusinessException("未找到产品序列号!");
                }
                if (Objects.nonNull(productSerialNumber.getPointId())) {
                    log.error("this productSerialNumber is binding other point productSerialNumberId:{}", entry.getKey());
                    throw new CustomBusinessException("产品已被使用!");
                }
                productSerialNumber.setPointId(point.getId());
                productSerialNumber.setSetNo(entry.getValue());
                productSerialNumberMapper.updateById(productSerialNumber);
                totalAmount = totalAmount.add(productSerialNumber.getPrice());
            }
            Point pointUpdate = new Point();
            pointUpdate.setId(point.getId());
            pointUpdate.setDeviceAmount(totalAmount);
            baseMapper.updateById(pointUpdate);
        }

        return R.ok();
    }

    /**
     * @param indexDataQuery
     * @return
     */
    @Override
    public IndexDataVo getCostIndexData(IndexDataQuery indexDataQuery) {
        IndexDataVo indexDataVo = new IndexDataVo();

        //运单费用
        BigDecimal deliverCostAmount = baseMapper.getDeliverCostAmount(indexDataQuery);
        deliverCostAmount = Objects.isNull(deliverCostAmount) ? BigDecimal.ZERO : deliverCostAmount;
        indexDataVo.setDeliverCostAmount(deliverCostAmount);
        //工单费用
        BigDecimal workOrderCostAmount = baseMapper.getWorkOrderCostAmount(indexDataQuery);
        workOrderCostAmount = Objects.isNull(workOrderCostAmount) ? BigDecimal.ZERO : workOrderCostAmount;
        indexDataVo.setWorkOrderCostAmount(workOrderCostAmount);
        BigDecimal allCostAmount = workOrderCostAmount.add(deliverCostAmount);
        indexDataVo.setAllCostAmount(allCostAmount);

        CabinetAndBoxAmountVo cabinetAndBoxAmountVo = baseMapper.getCabinetAmount(indexDataQuery);
//        Long boxAmount = baseMapper.getBoxAmount(indexDataQuery);
        indexDataVo.setCabinetAmount(Objects.isNull(cabinetAndBoxAmountVo) || Objects.isNull(cabinetAndBoxAmountVo.getCabinetAmount()) ? 0L : cabinetAndBoxAmountVo.getCabinetAmount());
        indexDataVo.setBoxAmount(Objects.isNull(cabinetAndBoxAmountVo) || Objects.isNull(cabinetAndBoxAmountVo.getBoxAmount()) ? 0L : cabinetAndBoxAmountVo.getBoxAmount());
        if (allCostAmount.compareTo(BigDecimal.ZERO) == 0) {
            indexDataVo.setSingleBoxCostAmount(BigDecimal.ZERO);
        } else {
            if (indexDataVo.getBoxAmount() < 1) {
                indexDataVo.setSingleBoxCostAmount(BigDecimal.ZERO);
            } else {
                indexDataVo.setSingleBoxCostAmount(allCostAmount.divide(new BigDecimal(String.valueOf(cabinetAndBoxAmountVo.getBoxAmount())), 2, BigDecimal.ROUND_HALF_UP));

            }
        }
        return indexDataVo;
    }

    /**
     * 绑定产品列表
     *
     * @param pointQuery
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R pointBindSerialNumber(PointQuery pointQuery) {
        Point point = getById(pointQuery.getId());
        if (Objects.isNull(point)) {
            return R.failMsg("未找到点位信息!");
        }
        if (ObjectUtil.isNotEmpty(pointQuery.getProductSerialNumberIdAndSetNoMap())) {
            BigDecimal totalAmount = Objects.isNull(point.getDeviceAmount()) ? BigDecimal.ZERO : point.getDeviceAmount();

            pointQuery.getProductSerialNumberIdAndSetNoMap().forEach((k,v) -> {
                ProductSerialNumber productSerialNumber = productSerialNumberMapper.selectById(k);
                if (Objects.isNull(productSerialNumber)) {
                    log.error("not found productSerialNumber by id:{}", k);
                    throw new CustomBusinessException("未找到产品序列号!");
                }

//                if (Objects.nonNull(productSerialNumber.getPointId())) {
//                    log.error("this productSerialNumber is binding other point productSerialNumberId:{}", entry.getKey());
//                    throw new CustomBusinessException("产品已被使用!");
//                }

                productSerialNumber.setPointId(point.getId());
                productSerialNumber.setSetNo(v);
                productSerialNumberMapper.updateById(productSerialNumber);
                if (Objects.nonNull(productSerialNumber.getPrice())) {
                    totalAmount.add(productSerialNumber.getPrice());
                }
            });

            Point pointUpdate = new Point();
            pointUpdate.setId(point.getId());
            pointUpdate.setDeviceAmount(totalAmount);
            baseMapper.updateById(pointUpdate);
        }
        return R.ok();

    }

    @Override
    public R reconciliationPage(Long offset, Long size, PointQuery point) {
        Page page = PageUtil.getPage(offset, size);
        return R.ok(baseMapper.pointPage(page, point));
    }

    /**
     * 导出excel
     * @param pointQuery
     * @param response
     */
    @Override
    public void exportExcel(PointQuery pointQuery, HttpServletResponse response) {
        List<PointVo> pointVoList = baseMapper.getPointList(pointQuery);

        if (ObjectUtil.isEmpty(pointVoList)) {
            throw new CustomBusinessException("未找到点位信息!");
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<PointExcelVo> pointExcelVoArrayList = new ArrayList<>(pointVoList.size());
        for (PointVo o : pointVoList) {
            PointExcelVo pointExcelVo = new PointExcelVo();
            BeanUtil.copyProperties(o, pointExcelVo);
            BigDecimal a = Objects.isNull(o.getDeviceAmount()) ? BigDecimal.ZERO : o.getDeviceAmount();
            BigDecimal b = Objects.isNull(o.getServerAmount()) ? BigDecimal.ZERO : o.getServerAmount();
            BigDecimal allAmount = a.add(b);
            pointExcelVo.setServerAmount(allAmount);

            pointExcelVoArrayList.add(pointExcelVo);
        }

        String fileName = "点位.xlsx";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            EasyExcel.write(outputStream, PointExcelVo.class).sheet("sheet").doWrite(pointExcelVoArrayList);
            return;
        } catch (IOException e) {
            log.error("导出报表失败！", e);
        }
        throw new CustomBusinessException("导出报表失败！请联系客服！");
    }

    @Override
    public List<PointVo> getlist() {
        return baseMapper.getAllPoint();
    }

    @Override
    public Integer getByStatCount(Map<String, Object> params) {
        String years = (String) params.get("years");
        String mouths = (String) params.get("mouths");
        String city = (String) params.get("city");

        Integer count = this.baseMapper.getByStatCount(years,mouths,city);
        return count;
    }
}
