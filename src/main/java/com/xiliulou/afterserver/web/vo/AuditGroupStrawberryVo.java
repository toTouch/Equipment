package com.xiliulou.afterserver.web.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zgw
 * @date 2022/6/10 11:34
 * @mood
 */
@Data
public class AuditGroupStrawberryVo {
    private Long id;
    private String name;
    private BigDecimal sort;
}
