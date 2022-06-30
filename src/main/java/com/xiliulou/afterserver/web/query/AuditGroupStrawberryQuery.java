package com.xiliulou.afterserver.web.query;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author zgw
 * @date 2022/6/10 11:44
 * @mood
 */
@Data
public class AuditGroupStrawberryQuery {
    private Long id;
    @NotNull(message = "分组名不能为空")
    private String name;
    @NotNull(message = "排序不能为空")
    private BigDecimal sort;
    @NotNull(message = "页面类型不能为空")
    private String processType;
}
