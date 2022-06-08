package com.xiliulou.afterserver.web.query;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zgw
 * @date 2022/6/8 14:49
 * @mood
 */
@Data
public class AuditEntryQuery {
    @NotNull(message = "组件id不能为空")
    private Long id;

    private String value;
}
