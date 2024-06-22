package com.xiliulou.afterserver.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
/**
 * (MaterialBatch)实体类
 *
 * @author zhangbozhi
 * @since 2024-06-19 15:06:15
 */
public class MaterialBatchExcelVo implements Serializable {
    
    private static final long serialVersionUID = 951731148988636592L;
    
    /**
     * 物料批次号
     */
    @ExcelProperty("批次号")
    private String materialBatchNo;
    
    /**
     * 物料编号id(物料型号编号id)
     */
    @ExcelProperty("物料型号")
    private String sn;
    
    /**
     * 物料型号名称
     */
    @ExcelProperty("物料型号名称")
    private String materialTypeName;
    
    /**
     * 供应商id
     */
    @ExcelProperty("供应商")
    private String supplierName;
    
    /**
     * 物料数量
     */
    @ExcelProperty("物料数量")
    private Integer materialCount;
    
    /**
     * 合格物料数
     */
    @ExcelProperty("合格数量")
    private Integer qualifiedCount;
    
    /**
     * 不合格物料数
     */
    @ExcelProperty("不合格数量")
    private Integer unqualifiedCount;
    
    /**
     * 批次备注
     */
    @ExcelProperty("批次备注")
    private String materialBatchRemark;
    
}

