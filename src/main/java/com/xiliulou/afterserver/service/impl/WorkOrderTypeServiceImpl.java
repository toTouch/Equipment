package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.WorkOrderType;
import com.xiliulou.afterserver.mapper.WorkOrderTypeMapper;
import com.xiliulou.afterserver.service.WorkOrderTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 10:42
 **/
@Service
@Slf4j
public class WorkOrderTypeServiceImpl extends ServiceImpl<WorkOrderTypeMapper, WorkOrderType> implements WorkOrderTypeService {
}
