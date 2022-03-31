package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiliulou.afterserver.entity.WorkOrder;
import com.xiliulou.afterserver.web.query.WorkOrderQuery;
import com.xiliulou.afterserver.web.vo.*;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface WorkOrderMapper extends BaseMapper<WorkOrder> {


    Page<WorkOrderVo> getPage(Page page, @Param("query") WorkOrderQuery workOrder);

    List<WorkOrderVo> orderList(@Param("query") WorkOrderQuery workOrder);

    List<AfterCountVo> qualityCount(@Param("pointId") Long pointId, @Param("cityId") Integer cityId,
                                    @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    List<AfterCountListVo> qualityCountList(@Param("pointId") Long pointId, @Param("cityId") Integer cityId,
                                            @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    List<AfterOrderVo> afterWorkOrderByCity(@Param("pointId") Long pointId, @Param("cityId") Integer cityId,
                                            @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    List<AfterOrderVo> afterWorkOrderByPoint(@Param("pointId") Long pointId, @Param("cityId") Integer cityId,
                                             @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    List<AfterOrderVo> afterWorkOrderList(@Param("pointId") Long pointId, @Param("cityId") Integer cityId,
                                          @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    List<AfterOrderVo> installWorkOrderByCity(@Param("pointId") Long pointId, @Param("cityId") Integer cityId,
                                              @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    List<AfterOrderVo> installWorkOrderByPoint(@Param("pointId") Long pointId, @Param("cityId") Integer cityId,
                                               @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    List<AfterOrderVo> installWorkOrderList(@Param("pointId") Long pointId, @Param("cityId") Integer cityId,
                                            @Param("startTime") Long startTime, @Param("endTime") Long endTime);

    Integer countOrderList(@Param("query") WorkOrderQuery workOrder);

    Integer updateOne(WorkOrder workOrder);

    @Update("update work_order set creater_id = #{createUid} where id = #{id}")
    Integer putAdminPointNewCreateUser(@Param("id")Long id, @Param("createUid") Long createUid);

    Page<WorkOrderAssignmentVo> queryAssignmentStatusList(Page page, @Param("uid") Long uid,@Param("list")List<Long> list,  @Param("status")Integer status);
}
