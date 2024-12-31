package com.xiliulou.afterserver.vo;

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
public class MaterialTraceabilityVO implements Serializable {
    
    
    /**
     * 物料sn
     */
    @ExcelProperty(value = "物料SN")
    private String materialSn;
    
    @ExcelProperty(value = "物料型号名称")
    private String name;
    
    @ExcelProperty(value = "物料编号")
    private String sn;
    
    /**
     * 物料批次No
     */
    @ExcelProperty(value = "物料批次号")
    private String materialBatchNo;

    /**
     * 产品编号/资产编码
     */
    @ExcelProperty(value = "资产编码")
    private String productNo;
    
    @FieldCompare(chineseName = "物料状态", properties = "0:不合格，1:合格")
    @ExcelProperty(value = "物料状态")
    private String materialState;
    /**
     * 创建时间
     */
    @ExcelProperty(value = "录入时间")
    private String createTime;
    
    @ExcelIgnore
    private String materialBatchId;
    
    
}

