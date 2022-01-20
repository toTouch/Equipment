package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_point_audit_remarks")
public class PointAuditRemarks {
    private Long id;
    private String remarks;
    private Long uid;
    private Long createTime;
    private Long updateTime;
    private Integer delFlag;

    public static final Integer DEL_NORMAL = 0;
    public static final Integer DEL_DEL = 1;
}
