package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.WorkOrderServer;
import com.xiliulou.afterserver.mapper.WorkOrderServerMapper;
import com.xiliulou.afterserver.service.WorkOrderServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zgw
 * @date 2022/3/29 18:13
 * @mood
 */
@Service
@Slf4j
public class WorkOrderServerServiceImpl extends ServiceImpl<WorkOrderServerMapper, WorkOrderServer> implements WorkOrderServerService {
    @Autowired
    WorkOrderServerMapper workOrderServerMapper;


}
