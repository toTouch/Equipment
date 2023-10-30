package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.WorkOrderType;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 10:42
 **/
public interface WorkOrderTypeService extends IService<WorkOrderType> {
    
    WorkOrderType queryByIdFromCache(Integer id);
}
