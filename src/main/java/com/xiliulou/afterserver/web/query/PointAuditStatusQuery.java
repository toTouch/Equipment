package com.xiliulou.afterserver.web.query;

import java.util.List;
import lombok.Data;

@Data
public class PointAuditStatusQuery {
    private Long id;
    private List<Long> ids;
    private Integer auditStatus;
    private String auditRemarks;
}
