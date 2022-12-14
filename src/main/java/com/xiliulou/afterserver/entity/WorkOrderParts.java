package com.xiliulou.afterserver.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * (WorkOrderParts)实体类
 *
 * @author Eclair
 * @since 2022-12-13 15:50:09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_work_order_parts")
public class WorkOrderParts {
    
    private Long id;
    
    private Long workOrderId;
    
    private Long serverId;
    
    private String name;
    
    private Integer sum;
    
    private Long createTime;
    
    private Long updateTime;

    public static final Integer DEL_NORMAL = 0;
    public static final Integer DEL_DEL = 1;

}
