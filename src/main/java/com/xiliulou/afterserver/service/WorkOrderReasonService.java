package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.WorkOrderReason;
import com.xiliulou.afterserver.util.R;

public interface WorkOrderReasonService extends IService<WorkOrderReason> {
    Integer deleteById(Long id);

    R getTreeList();
    
    WorkOrderReason queryByIdFromCache(Long workOrderReasonId);
}
