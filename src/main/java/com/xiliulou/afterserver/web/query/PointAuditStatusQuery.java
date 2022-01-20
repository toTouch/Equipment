package com.xiliulou.afterserver.web.query;

import lombok.Data;

@Data
public class PointAuditStatusQuery {
    private Long id;
    private Integer auditStatus;
    private Integer auditRemarks;
}
