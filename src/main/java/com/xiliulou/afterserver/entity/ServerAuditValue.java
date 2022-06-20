package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author zgw
 * @date 2022/6/20 11:11
 * @mood
 */
@Data
@TableName("t_server_audit_value")
public class ServerAuditValue {
    private Long id;
    /**
     * 工单id
     */
    private Long orderId;
    /**
     * 服务商id
     */
    private Long serverId;
    /**
     * 组件表外键
     */
    private  Long entryId;
    /**
     * 组件值
     */
    private String value;

    private Long updateTime;

    private Long createTime;
}
