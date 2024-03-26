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
 * 物料追溯表(MaterialTraceability)实体类
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
     * 主键ID
     */
    private Long id;
    
    /**
     * 物料sn
     */
    @ExcelProperty(value = "物料编码")
    private String materialSn;
    
    /**
     * 产品编号/资产编码
     */
    @ExcelProperty(value = "资产编码")
    private String productNo;
    
    /**
     * 创建时间
     */
    @ExcelProperty(value = "绑定时间")
    private String createTime;
    
    
}

