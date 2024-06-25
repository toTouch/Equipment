package com.xiliulou.afterserver.web.query;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiliulou.afterserver.plugin.FieldCompare;
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
public class MaterialQuery implements Serializable {
    
    private static final long serialVersionUID = 891201978381378054L;
    
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
    
    private String imei;
    
    private String atmelID;
    
    private String testTime;
    
    /**
     * 备注
     */
    @FieldCompare(chineseName = "备注")
    private String remark;
    
    /**
     * 物料批次No
     */
    private String materialBatchNo;
    
    /**
     * 物料型号id
     */
    private Long materialId;
    
    /**
     * 物料状态(0-不合格，1-合格)
     */
    private Integer materialState;
    
    @TableField(exist = false)
    @ExcelIgnore
    private String name;
    
    @TableField(exist = false)
    @ExcelIgnore
    private String sn;
    
    
    @TableField(exist = false)
    private Long startTime;
    
    @TableField(exist = false)
    private Long endTime;
}

