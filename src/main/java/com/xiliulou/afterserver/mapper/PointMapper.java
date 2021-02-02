package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiliulou.afterserver.entity.Point;
import com.xiliulou.afterserver.web.query.IndexDataQuery;
import com.xiliulou.afterserver.web.query.PointQuery;
import com.xiliulou.afterserver.web.vo.PointVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 18:59
 **/
public interface PointMapper extends BaseMapper<Point> {

    Page<PointVo> pointPage(Page page, @Param("query") PointQuery point);

    BigDecimal getDeliverCostAmount(@Param("query") IndexDataQuery indexDataQuery);

    BigDecimal getWorkOrderCostAmount(@Param("query") IndexDataQuery indexDataQuery);

    Long getCabinetAmount(@Param("query") IndexDataQuery indexDataQuery);
}
