package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 10:47
 **/
@Data
@TableName("work_order_reason")
public class WorkOrderReason {

    private Long id;
    private String name;
    private Long parentId;
}
