package com.xiliulou.afterserver.web.query;

import lombok.Data;

@Data
public class WorkOrderAuditStatusQuery {
    private Long id;
    private Integer auditStatus;
    private String auditRemarks;
}
