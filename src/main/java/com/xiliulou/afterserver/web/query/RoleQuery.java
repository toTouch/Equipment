package com.xiliulou.afterserver.web.query;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
@Data
public class RoleQuery {

    @NotNull(message = "id不能为空")
    private Long id;

    @NotEmpty(message = "角色名称不能为空")
    private String name;

    @NotEmpty(message = "角色code不能为空")
    private String code;
}
