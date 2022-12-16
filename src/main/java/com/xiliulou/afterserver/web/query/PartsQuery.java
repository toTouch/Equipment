package com.xiliulou.afterserver.web.query;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author zgw
 * @date 2022/12/15 15:21
 * @mood
 */
@Data
public class PartsQuery {
    private Long id;
    @NotNull(message="请填写物料名称")
    private String name;
    @NotNull(message="请填写物料单价")
    private BigDecimal price;
}
