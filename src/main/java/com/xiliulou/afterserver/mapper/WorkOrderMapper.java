package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiliulou.afterserver.entity.WorkOrder;
import com.xiliulou.afterserver.web.query.WorkOrderQuery;
import com.xiliulou.afterserver.web.vo.ReconciliationSummaryVo;
import com.xiliulou.afterserver.web.vo.WorkOrderVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WorkOrderMapper extends BaseMapper<WorkOrder> {


    Page<WorkOrderVo> getPage(Page page, @Param("query") WorkOrderQuery workOrder);

    List<WorkOrderVo> orderList(@Param("query") WorkOrderQuery workOrder);

    List<ReconciliationSummaryVo> reconciliationSummary(@Param("query") WorkOrderQuery workOrder);
}
