package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.WorkOrderServer;
import com.xiliulou.afterserver.web.query.WorkOrderServerQuery;

import java.util.List;

/**
 * @author zgw
 * @date 2022/3/29 18:12
 * @mood
 */
public interface WorkOrderServerService extends IService<WorkOrderServer> {

    List<WorkOrderServerQuery> queryByWorkOrderId(Long workOrderId);

    Boolean removeByWorkOrderId(Long id);
}
