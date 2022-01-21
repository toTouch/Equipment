package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_point_audit_remarks")
public class PointAuditRemarks {
    private Long id;
    private String remarks;
    private Integer type;
    private Long uid;
    private Long createTime;
    private Long updateTime;
    private Integer delFlag;

    public static final Integer DEL_NORMAL = 0;
    public static final Integer DEL_DEL = 1;

    public static final Integer TYPE_POINT_NEW = 1;
    public static final Integer TYPE_WORK_ORDER= 2;
}
