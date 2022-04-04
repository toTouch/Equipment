package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zgw 工单审核通知
 * @date 2022/4/2 17:03
 * @mood
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_work_audit_notify")
public class WorkAuditNotify {
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

    public static final Integer TYPE_FINISH = 1;
    public static final Integer TYPE_UNFINISHED = 0;
}
