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

    List<WorkOrderServerQuery> queryByWorkOrderIdAndServerId(Long workOrderId, Long serverId);

    Boolean removeByWorkOrderId(Long id);

    List<Long> queryWorkOrderIds(Long serverId);

    Boolean updateSolutionByWorkOrderAndServerId(Long workOrderId, Long thirdId, String solution);

    Long queryPrescriptionAvgByServerId(Long curtMonthTime, Long id);

    Integer queryMaxCountByWorkOrderId(List<Long> workOrderIds);

    List<WorkOrderServerQuery> queryByWorkOrderId(Long workOrderId);

    List<Integer> getIdsByserverIds(List<Integer> serverIds);
}
