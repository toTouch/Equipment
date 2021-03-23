package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiliulou.afterserver.entity.Point;
import com.xiliulou.afterserver.web.query.IndexDataQuery;
import com.xiliulou.afterserver.web.query.PointQuery;
import com.xiliulou.afterserver.web.vo.CabinetAndBoxAmountVo;
import com.xiliulou.afterserver.web.vo.PointVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

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

    CabinetAndBoxAmountVo getCabinetAmount(@Param("query") IndexDataQuery indexDataQuery);

    Long getBoxAmount(@Param("query") IndexDataQuery indexDataQuery);

    List<PointVo> getPointList(@Param("query") PointQuery pointQuery);

    Page<Long> getPointIdList(Page page, @Param("query") PointQuery point);

    PointVo getPointBaseInfo(@Param("id") Long pointId);

    List<PointVo> getAllPoint();

    Integer getByStatCount(@Param("years") String years, @Param("mouths") String mouths, @Param("city") String city);

}
