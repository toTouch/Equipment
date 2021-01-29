package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.Point;
import com.xiliulou.afterserver.entity.User;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 19:04
 **/
public interface PointService extends IService<Point> {
    IPage getPage(Long offset, Long size, Point point);
}
