package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

/**
 * @program: XILIULOU
 * @description: 工单原因
 * @author: Mr.YG
 * @create: 2021-01-29 10:47
 **/
@Data
@TableName("work_order_reason")
public class WorkOrderReason {

    private Long id;
    private String name;
    private Long parentId;



    @TableField(exist = false)
    private List<WorkOrderReason> chird;
    public static final Long PARENT_NODE = -1L;
}
