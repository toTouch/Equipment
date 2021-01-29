package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.Point;
import com.xiliulou.afterserver.mapper.PointMapper;
import com.xiliulou.afterserver.service.PointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 19:04
 **/
@Service
@Slf4j
public class PointServiceImpl extends ServiceImpl<PointMapper, Point> implements PointService {


    @Override
    public IPage getPage(Long offset, Long size, Point point) {
        return null;
    }
}
