package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zgw
 * @date 2022/6/22 17:39
 * @mood
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_audit_group_log_content")
public class AuditGroupLogContent {
    private Long id;
    private Long logId;
    private String entryName;
    private Integer entryType;
    private String value;
}
