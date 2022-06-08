package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author zgw
 * 组件分组（t_audit_group）
 * @date 2022/6/6 16:38
 * @mood
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_audit_group")
public class AuditGroup {
    private Long id;
    /**
     * 分组名
     */
    private String name;
    /**
     * 组件jds  (json:['1'])
     */
    private String entryIds;
    /**
     *分组流程顺序
     */
    private BigDecimal sort;
    /**
     * 页面id
     */
    private Long processId;
}
