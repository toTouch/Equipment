package com.xiliulou.afterserver.web.query;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author zgw
 * @date 2022/6/10 15:10
 * @mood
 */
@Data
public class AuditEntryStrawberryQuery {
    private Long id;
    private String jsonRoot;

    @NotNull(message = "组件名不能为空")
    private String name;
    @NotNull(message = "组件类型不能为空")
    private Integer type;
    @NotNull(message = "排序不能为空")
    private BigDecimal sort;
    @NotNull(message = "是否必填不能为空")
    private Integer required;
    @NotNull(message = "模块id不能为空")
    private Long groupId;
}
