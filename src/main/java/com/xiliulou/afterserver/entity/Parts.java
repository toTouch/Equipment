package com.xiliulou.afterserver.entity;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * (Parts)实体类
 *
 * @author Eclair
 * @since 2022-12-15 15:02:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_parts")
public class Parts {
    
    private Long id;
    
    private String name;
    /**
     * 采购价
     */
    private BigDecimal purchasePrice;
    /**
     * 出售价
     */
    private BigDecimal sellPrice;
    /**
     * 规格
     */
    private String specification;

    private String sn;
    /**
     * 物料类型
     */
    private String materialType;
    
    /**
     * 物料别名
     */
    private String materialAlias;
    
    private Long createTime;
    
    private Long updateTime;

    private Integer delFlag;

    public static final Integer DEL_NORMAL = 0;
    public static final Integer DEL_DEL = 1;

}
