package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.WorkOrderReason;

public interface WorkOrderReasonService extends IService<WorkOrderReason> {
    Integer deleteById(Long id);
}
