package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 10:40
 **/
@Data
@TableName("")
public class WorkOrderType {

    private Long id;
    private String Type;
    private String remark;
}
