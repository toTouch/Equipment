package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.File;
import com.xiliulou.afterserver.entity.Point;
import com.xiliulou.afterserver.entity.PointBindProduct;
import com.xiliulou.afterserver.mapper.PointBindProductMapper;
import com.xiliulou.afterserver.mapper.PointMapper;
import com.xiliulou.afterserver.service.FileService;
import com.xiliulou.afterserver.service.PointService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.PointQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public IPage getPage(Long offset, Long size, Point point) {
        return baseMapper.pointPage(PageUtil.getPage(offset, size), point);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R savePoint(PointQuery pointQuery) {
        Point point = new Point();
        BeanUtils.copyProperties(pointQuery, point);
        baseMapper.insert(point);

        if (ObjectUtil.isNotEmpty(pointQuery.getFileNameList())) {
            List<File> filList = new ArrayList();
            for (String name : pointQuery.getFileNameList()) {
                File file = new File();
                file.setFileName(name);
                file.setBindId(point.getId());
                file.setType(File.TYPE_POINT);
                file.setCreateTime(System.currentTimeMillis());
                filList.add(file);
            }
            fileService.saveBatch(filList);
        }
        if (ObjectUtil.isNotEmpty(pointQuery.getProductIdList())) {
            for (Long id : pointQuery.getProductIdList()) {
                PointBindProduct productBindProduct = new PointBindProduct();
                productBindProduct.setPointId(point.getId());
                productBindProduct.setProductId(id);
                pointBindProductMapper.insert(productBindProduct);
            }
        }
        return R.ok();
    }
}
