package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.Point;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.IndexDataQuery;
import com.xiliulou.afterserver.web.query.PointQuery;
import com.xiliulou.afterserver.web.vo.IndexDataVo;
import com.xiliulou.afterserver.web.vo.PointVo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 19:04
 **/
public interface PointService extends IService<Point> {
    IPage getPage(Long offset, Long size, PointQuery point);

    R savePoint(PointQuery pointQuery);

    IndexDataVo getCostIndexData(IndexDataQuery indexDataQuery);

    R pointBindSerialNumber(PointQuery pointQuery);

    R reconciliationPage(Long offset, Long size, PointQuery point);

    void exportExcel(PointQuery pointQuery, HttpServletResponse response);

    List<PointVo> getlist();

    R unBindSerialNumber(Long pid);

    R pointGetBingList(Long id);

    R updatePoint(PointQuery point);
}
