package com.xiliulou.afterserver.entity.mq.notify;

import lombok.Data;

/**
 * @author zgw
 * @date 2022/4/28 15:32
 * @mood
 */
@Data
public class MqWorkOrderServerNotify {
    private String workOrderNo;
    private String orderTypeName;
    private String pointName;
    private String assignmentTime;
}
