package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiliulou.afterserver.entity.WorkOrder;
import com.xiliulou.afterserver.web.query.WorkOrderQuery;
import com.xiliulou.afterserver.web.vo.AfterCountListVo;
import com.xiliulou.afterserver.web.vo.AfterCountVo;
import com.xiliulou.afterserver.web.vo.AfterOrderVo;
import com.xiliulou.afterserver.web.vo.WorkOrderVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WorkOrderMapper extends BaseMapper<WorkOrder> {


    Page<WorkOrderVo> getPage(Page page, @Param("query") WorkOrderQuery workOrder);

    List<WorkOrderVo> orderList(@Param("query") WorkOrderQuery workOrder);

    List<AfterCountVo> qualityCount(@Param("pointId") Long pointId, @Param("cityId") Integer cityId, @Param("datestamp") Long datestamp);

    List<AfterCountListVo> qualityCountList(@Param("pointId") Long pointId, @Param("cityId") Integer cityId, @Param("datestamp") Long datestamp);

    List<AfterOrderVo> afterWorkOrderByCity(@Param("pointId") Long pointId, @Param("cityId") Integer cityId, @Param("datestamp") Long datestamp);

    List<AfterOrderVo> afterWorkOrderByPoint(@Param("pointId") Long pointId, @Param("cityId") Integer cityId, @Param("datestamp") Long datestamp);

    List<AfterOrderVo> afterWorkOrderList(@Param("pointId") Long pointId, @Param("cityId") Integer cityId, @Param("datestamp") Long datestamp);

    List<AfterOrderVo> installWorkOrderByCity(@Param("pointId") Long pointId, @Param("cityId") Integer cityId, @Param("datestamp") Long datestamp);

    List<AfterOrderVo> installWorkOrderByPoint(@Param("pointId") Long pointId, @Param("cityId") Integer cityId, @Param("datestamp") Long datestamp);

    List<AfterOrderVo> installWorkOrderList(@Param("pointId") Long pointId, @Param("cityId") Integer cityId, @Param("datestamp") Long datestamp);
}
