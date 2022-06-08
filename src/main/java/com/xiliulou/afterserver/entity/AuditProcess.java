package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zgw
 * 页面（t_audit_process）
 * @date 2022/6/6 17:06
 * @mood
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_audit_process")
public class AuditProcess {
    private Long id;
    /**
     * 页面名
     */
    private String name;
    /**
     * 区分页面的type
     */
    private String type;

    public static final String TYPE_PRE = "PRE";
    public static final String TYPE_POST = "POST";
}
