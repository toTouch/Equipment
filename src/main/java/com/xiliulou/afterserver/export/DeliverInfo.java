package com.xiliulou.afterserver.export;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Hardy
 * @date 2021/8/31 14:30
 * @mood
 */
@Data
public class DeliverInfo {

    @ExcelProperty(index = 0)
    private String startPoint;

    @ExcelProperty(index = 1)
    private String endPoint;

    @ExcelProperty(index = 2)
    private Integer status;

    @ExcelProperty(index = 3)
    private Long thirdCompanyId;

    @ExcelProperty(index = 4)
    private Integer thirdCompanyType;

    @ExcelProperty(index = 5)
    private BigDecimal thirdCompanyPay;

    @ExcelProperty(index = 6)
    private String thirdReason;

    @ExcelProperty(index = 7)
    private BigDecimal cost;

    @ExcelProperty(index = 8)
    private String expressName;

    @ExcelProperty(index = 9)
    private String expressNo;

    @ExcelProperty(index = 10)
    private String deliverTime;

    @ExcelProperty(index = 11)
    private String remarks;

    @ExcelProperty(index = 12)
    private String product;//型号

    @ExcelProperty(index = 13)
    private String quantity;//数量



}
