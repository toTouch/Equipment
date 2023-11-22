package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.constant.cache.WorkOrderConstant;
import com.xiliulou.afterserver.entity.WorkOrderType;
import com.xiliulou.afterserver.mapper.WorkOrderTypeMapper;
import com.xiliulou.afterserver.service.WorkOrderTypeService;
import com.xiliulou.cache.redis.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 10:42
 **/
@Service
@Slf4j
public class WorkOrderTypeServiceImpl extends ServiceImpl<WorkOrderTypeMapper, WorkOrderType> implements WorkOrderTypeService {
    
    @Autowired
    private RedisService redisService;
    
    @Override
    public WorkOrderType queryByIdFromCache(Integer id) {
        WorkOrderType serviceWithHash = redisService.getWithHash(WorkOrderConstant.WORK_ORDER_TYPE + id, WorkOrderType.class);
        if (Objects.nonNull(serviceWithHash)) {
            return serviceWithHash;
        }
        
        WorkOrderType workOrderType = this.getById(id);
        if (Objects.isNull(workOrderType)) {
            return null;
        }
        
        redisService.saveWithHash(WorkOrderConstant.WORK_ORDER_TYPE + id, workOrderType);
        return workOrderType;
    }
}
