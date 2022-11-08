package com.xiliulou.afterserver.entity.mq.notify;

import lombok.Data;

/**
 * @author zgw
 * @date 2022/11/8 15:52
 * @mood
 */
@Data
public class MqPointNewAuditNotify {
    private String pointName;
    private String auditUserName;
    private String auditRemark;
    private String auditTime;
}
