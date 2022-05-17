package com.xiliulou.afterserver.web.query;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zgw
 * @date 2022/5/16 14:53
 * @mood
 */
@Data
public class MaintenanceUserNotifyConfigQuery {
    @NotNull(message = "手机号不可以为空")
    private String phones;

    @NotNull(message = "权限不可以为空")
    private Integer permission;

    @NotNull(message = "类型不可以为空")
    private Integer type;

    private Long bindId;
}
