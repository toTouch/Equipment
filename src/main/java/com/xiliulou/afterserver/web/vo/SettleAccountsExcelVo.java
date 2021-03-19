package com.xiliulou.afterserver.web.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettleAccountsExcelVo {

    @ExcelProperty("客户")
    private Long customerId;
    @ExcelProperty("付款金额")
    private BigDecimal payAmount;
    @ExcelProperty("未付款金额")
    private BigDecimal unPayAmount;
    @ExcelProperty("总金额")
    private BigDecimal totalAmount;
    @ExcelProperty("备注")
    private String remark;
    @ExcelProperty("支付时间")
    private Long payTime;
    @ExcelProperty("创建时间")
    private Long createTime;
}
