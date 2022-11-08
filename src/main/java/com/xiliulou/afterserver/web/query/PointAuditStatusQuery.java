package com.xiliulou.afterserver.web.query;

import java.util.List;
import lombok.Data;

@Data
public class PointAuditStatusQuery {
    private Long id;
    private List<Long> ids;
    private Integer auditStatus;
    private String auditRemarks;
    /**
     * 审核人id
     */
    private Long auditUid;
    /**
     * 审核人
     */
    private String auditUserName;
    /**
     * 审核时间
     */
    private Long auditTime;
}
