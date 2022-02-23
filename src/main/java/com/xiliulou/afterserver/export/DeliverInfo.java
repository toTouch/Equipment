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
    private String startPointType;

    @ExcelProperty(index = 1)
    private String startPoint;

    @ExcelProperty(index = 2)
    private String endPointType;

    @ExcelProperty(index = 3)
    private String endPoint;

    @ExcelProperty(index = 4)
    private String status;

    @ExcelProperty(index = 5)
    private String thirdCompanyId;

    @ExcelProperty(index = 6)
    private Integer thirdCompanyType;

    @ExcelProperty(index = 7)
    private BigDecimal thirdCompanyPay;

    @ExcelProperty(index = 8)
    private String thirdReason;

    @ExcelProperty(index = 9)
    private BigDecimal cost;

    @ExcelProperty(index = 10)
    private String paymentMethod;

    @ExcelProperty(index = 11)
    private String expressName;

    @ExcelProperty(index = 12)
    private String expressNo;

    @ExcelProperty(index = 13)
    private String deliverTime;

    @ExcelProperty(index = 14)
    private String remarks;

    @ExcelProperty(index = 15)
    private String product;//型号

    @ExcelProperty(index = 16)
    private String quantity;//数量



}
