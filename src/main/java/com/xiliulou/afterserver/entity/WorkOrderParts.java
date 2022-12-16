package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import java.math.BigDecimal;
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

    /**
     * 类型 1--服务商物件  2--第三方物件
     */
    private Integer type;

    private BigDecimal amount;
    
    private Long createTime;
    
    private Long updateTime;

    public static final Integer DEL_NORMAL = 0;
    public static final Integer DEL_DEL = 1;

    public static final Integer TYPE_SERVER_PARTS = 1;
    public static final Integer TYPE_THIRD_PARTS = 2;

    @TableField(exist = false)
    private Long partsId;
}
