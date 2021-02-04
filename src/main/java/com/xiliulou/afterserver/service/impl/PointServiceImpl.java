package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.xml.bind.v2.model.core.ID;
import com.xiliulou.afterserver.entity.File;
import com.xiliulou.afterserver.entity.Point;
import com.xiliulou.afterserver.entity.PointBindProduct;
import com.xiliulou.afterserver.entity.ProductSerialNumber;
import com.xiliulou.afterserver.exception.CusTomBusinessAccessDeniedException;
import com.xiliulou.afterserver.exception.CustomBusinessException;
import com.xiliulou.afterserver.mapper.PointBindProductMapper;
import com.xiliulou.afterserver.mapper.PointMapper;
import com.xiliulou.afterserver.mapper.ProductSerialNumberMapper;
import com.xiliulou.afterserver.service.FileService;
import com.xiliulou.afterserver.service.PointService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.IndexDataQuery;
import com.xiliulou.afterserver.web.query.PointQuery;
import com.xiliulou.afterserver.web.vo.CabinetAndBoxAmountVo;
import com.xiliulou.afterserver.web.vo.IndexDataVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.channels.FileLock;
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

    @Override
    public IPage getPage(Long offset, Long size, PointQuery point) {
        return baseMapper.pointPage(PageUtil.getPage(offset, size), point);
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
        indexDataVo.setDeliverCostAmount(Objects.isNull(deliverCostAmount) ? BigDecimal.ZERO : deliverCostAmount);
        //工单费用
        BigDecimal workOrderCostAmount = baseMapper.getWorkOrderCostAmount(indexDataQuery);
        workOrderCostAmount = Objects.isNull(workOrderCostAmount) ? BigDecimal.ZERO : workOrderCostAmount;
        indexDataVo.setWorkOrderCostAmount(workOrderCostAmount);
        BigDecimal allCostAmount = workOrderCostAmount.add(deliverCostAmount);
        indexDataVo.setAllCostAmount(allCostAmount);

        CabinetAndBoxAmountVo cabinetAndBoxAmountVo = baseMapper.getCabinetAmount(indexDataQuery);
//        Long boxAmount = baseMapper.getBoxAmount(indexDataQuery);
        indexDataVo.setCabinetAmount(Objects.isNull(cabinetAndBoxAmountVo.getCabinetAmount()) ? 0L : cabinetAndBoxAmountVo.getCabinetAmount());
        indexDataVo.setBoxAmount(Objects.isNull(cabinetAndBoxAmountVo.getBoxAmount()) ? 0L : cabinetAndBoxAmountVo.getBoxAmount());
        if (allCostAmount.compareTo(BigDecimal.ZERO) == 0) {
            indexDataVo.setSingleBoxCostAmount(BigDecimal.ZERO);
        } else {
            if (cabinetAndBoxAmountVo.getBoxAmount() < 1) {
                throw new CusTomBusinessAccessDeniedException("格口总数小于1");
            }
            indexDataVo.setSingleBoxCostAmount(allCostAmount.divide(new BigDecimal(String.valueOf(cabinetAndBoxAmountVo.getBoxAmount())), 2, BigDecimal.ROUND_HALF_UP));
        }
        return indexDataVo;
    }
}
