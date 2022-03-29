package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.WorkOrderServer;
import com.xiliulou.afterserver.web.query.WorkOrderServerQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zgw
 * @date 2022/3/29 18:06
 * @mood
 */
public interface WorkOrderServerMapper extends BaseMapper<WorkOrderServer> {

    List<WorkOrderServerQuery> queryByWorkOrderId(@Param("workOrderId") Long workOrderId);
}
