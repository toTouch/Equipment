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
public class WorkOrderExcelVo3 {

    @ExcelProperty("类别")
    private String thirdCompanyType;
    @ExcelProperty("第三方公司")
    private String thirdCompanyName;
    @ExcelProperty("服务商")
    private String serverName;
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
    @ExcelProperty("结算方式")
    private String paymentMethodName;
    @ExcelProperty("第三方结算状态")
    private String thirdPaymentStatus;
    @ExcelProperty("备注")
    private String remarks;
    @ExcelProperty("描述")
    private String describeinfo;
    @ExcelProperty("创建时间")
    private String createTimeStr;
    @ExcelProperty("处理时间")
    private String processTimeStr;
}
