package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.WorkOrderReason;
import org.apache.ibatis.annotations.Param;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 10:48
 **/
public interface WorkOrderReasonMapper extends BaseMapper<WorkOrderReason> {
    Integer deleteForID(@Param("id") Long id);
}
