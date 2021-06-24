package com.xiliulou.afterserver.web.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-01 10:45
 **/
@Data
public class WorkOrderExcelVo {

    @ExcelProperty("类别")
    private String thirdCompanyType;
    @ExcelProperty("公司")
    private String thirdCompanyName;
    @ExcelProperty("工单类型")
    private String workOrderType;
    @ExcelProperty("点位")
    private String pointName;
    @ExcelProperty("工单原因")
    private String workOrderReasonName;
//    @ExcelProperty("工单状态")
//    private String statusStr;
    @ExcelProperty("费用")
    private BigDecimal thirdCompanyPay;
    @ExcelProperty("备注")
    private String remarks;
    @ExcelProperty("创建时间")
    private String createTimeStr;
}
