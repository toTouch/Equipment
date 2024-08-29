package com.xiliulou.afterserver.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * (Parts)实体类
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PartsExcelVo {
    @ExcelIgnore
    private Long id;
    @ExcelProperty(value = "物料型号名称")
    private String name;
    
    @ExcelProperty(value = "物料编号")
    private String sn;
    /**
     * 采购价
     */
    @ExcelProperty(value = "采购价格")
    private BigDecimal purchasePrice;
    /**
     * 出售价
     */
    @ExcelProperty(value = "售卖价格")
    private BigDecimal sellPrice;
    /**
     * 规格
     */
    @ExcelProperty(value = "物料规格")
    private String specification;
   
    /**
     * 物料类型
     */
    @ExcelProperty(value = "物料类别")
    private String materialType;
    
    /**
     * 物料别名
     */
    @ExcelProperty(value = "物料别名")
    private String materialAlias;

    public static final Integer DEL_NORMAL = 0;
    public static final Integer DEL_DEL = 1;

}
