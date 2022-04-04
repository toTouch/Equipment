package com.xiliulou.afterserver.web.vo;

import lombok.Data;

/**
 * @author zgw
 * @date 2022/4/2 18:54
 * @mood
 */
@Data
public class WorkAuditNotifyVo {
    private Long id;
    /**
     * 工单编号
     */
    private String orderNo;
    /**
     * 工单类型
     */
    private String orderType;
    /**
     * 工单原因
     */
    private Long workOrderReasonId;
    /**
     * 提交审核人
     */
    private Long submitUid;
    /**
     * 0 未读 1 已读
     */
    private Integer type;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 更新时间
     */
    private Long updateTime;

    private String workOrderReasonName;
    private String parentWorkOrderReasonName;
    private String orderTypeName;
    private String submitUserName;
}
