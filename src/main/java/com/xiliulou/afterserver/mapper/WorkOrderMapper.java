package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiliulou.afterserver.entity.WorkOrder;
import com.xiliulou.afterserver.web.vo.WorkOrderVo;

import java.util.List;

public interface WorkOrderMapper extends BaseMapper<WorkOrder> {


    Page<WorkOrderVo> getPage(Page page, WorkOrder workOrder);

    List<WorkOrderVo> orderList(WorkOrder workOrder);
}
