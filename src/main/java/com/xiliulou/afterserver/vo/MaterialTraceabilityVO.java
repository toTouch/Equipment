package com.xiliulou.afterserver.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 物料追溯表(Material)实体类
 *
 * @author makejava
 * @since 2024-03-21 11:33:12
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MaterialTraceabilityVO implements Serializable {
    
    
    /**
     * 物料sn
     */
    @ExcelProperty(value = "物料SN")
    private String materialSn;
    
    /**
     * 产品编号/资产编码
     */
    @ExcelProperty(value = "产品编号")
    private String productNo;
    
    /**
     * 创建时间
     */
    @ExcelProperty(value = "录入时间")
    private String createTime;
    
    
}

