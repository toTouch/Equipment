package com.xiliulou.afterserver.export;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author zgw
 * @date 2022/2/8 11:04
 * @mood
 */
@Data
public class IotCardInfo {
    /**
     * 卡号
     */
    @ExcelProperty(index = 0)
    private String sn;
    /**
     * 批次
     */
    @ExcelProperty(index = 1)
    private String batchName;
    /**
     * 供应商
     */
    @ExcelProperty(index = 2)
    private String supplierName;
    /**
     * 运营商
     */
    @ExcelProperty(index = 3)
    private String operatorName;
    /**
     * 激活时间
     */
    @ExcelProperty(index = 4)
    private String activationTime;
    /**
     * 套餐
     */
    @ExcelProperty(index = 5)
    private String packages;
    /**
     * 有效时间
     */
    @ExcelProperty(index = 6)
    private Long termOfAlidity;
}
