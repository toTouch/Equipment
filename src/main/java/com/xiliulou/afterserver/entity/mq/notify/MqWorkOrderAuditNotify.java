package com.xiliulou.afterserver.entity.mq.notify;

import lombok.Data;

/**
 * @author zgw
 * @date 2022/4/28 11:38
 * @mood
 */
@Data
public class MqWorkOrderAuditNotify {
    private String workOrderNo;
    private String orderTypeName;
    private String pointName;
    private String submitUName;
    private String submitTime;
}
