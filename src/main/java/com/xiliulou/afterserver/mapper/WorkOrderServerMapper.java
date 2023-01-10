package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.WorkOrderServer;
import com.xiliulou.afterserver.web.query.WorkOrderServerQuery;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author zgw
 * @date 2022/3/29 18:06
 * @mood
 */
public interface WorkOrderServerMapper extends BaseMapper<WorkOrderServer> {

    List<WorkOrderServerQuery> queryByWorkOrderId(@Param("workOrderId") Long workOrderId, @Param("serverId")Long serverId);

    @Select("SELECT work_order_id FROM t_work_order_server WHERE server_id = #{serverId}")
    List<Long> queryWorkOrderIds(Long serverId);

    @Update("update t_work_order_server set solution = #{solution} where work_order_id = #{workOrderId} and server_id = #{thirdId}")
    Boolean updateSolutionByWorkOrderAndServerId(@Param("workOrderId") Long workOrderId, @Param("thirdId")Long thirdId, @Param("solution")String solution);

    @Select("select avg(prescription) from t_work_order_server wos left join work_order wo on wos.work_order_id = wo.id  where wo.assignment_time >= #{curtMonthTime} and wos.server_id = #{id}")
    Long queryPrescriptionAvgByServerId(@Param("curtMonthTime") Long curtMonthTime, @Param("id")Long id);

    Integer queryMaxCountByWorkOrderId(@Param("workOrderIds") List<Long> workOrderIds);

    List<Integer> getIdsByserverIds(@Param("serverIds")List<Integer> serverIds);
}
