package com.xiliulou.afterserver.web.vo;

import java.math.BigDecimal;
import lombok.Data;

/**
 * @author zgw
 * @date 2022/12/15 15:54
 * @mood
 */
@Data
public class PartsVo {
    private Long id;
    private String name;
    private BigDecimal price;
}
