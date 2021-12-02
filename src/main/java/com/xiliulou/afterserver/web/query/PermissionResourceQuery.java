package com.xiliulou.afterserver.web.query;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class PermissionResourceQuery {
    @NotNull(message = "id不能为空")
    private long id;

    @NotEmpty(message = "名称不能为空")
    private String name;

    @NotNull(message = "类型不能为空")
    @Range(min = 1, max = 2, message = "type类型不合法")
    private Integer type;

    @NotEmpty(message = "uri不能为空")
    private String uri;
    @NotEmpty(message = "方法不能为空")
    private String method;

    @NotNull(message = "排序不能为空")
    private Double sort;

    private String desc;

    @NotNull(message = "父id不能为空")
    private Long parent;
}
