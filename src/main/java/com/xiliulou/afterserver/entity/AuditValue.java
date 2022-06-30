package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zgw
 * 控件值表（t_audit_value）
 * @date 2022/6/6 16:33
 * @mood
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_audit_value")
public class AuditValue {
    private Long id;
    /**
     * 产品id
     */
    private Long pid;
    /**
     * 组件id
     */
    private Long entryId;
    /**
     * 组件值
     */
    private String value;

    private Long updateTime;
    private Long createTime;
}
