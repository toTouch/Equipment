package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zgw
 * @date 2022/6/22 17:35
 * @mood
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_audit_group_update_log")
public class AuditGroupUpdateLog {
    private Long id;
    private Long groupId;
    private Long pid;
    private Long uid;
    private Long createTime;
}
