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

    @ExcelProperty("工单类型")
    private String workOrderType;
    @ExcelProperty("点位")
    private String thirdCompanyName;
    @ExcelProperty("费用")
    private BigDecimal fee;
    @ExcelProperty("公司")
    private String thirdCompanyId;
    @ExcelProperty("第三方承担费用")
    private BigDecimal thirdCompanyPay;
    @ExcelProperty("工单原因")
    private String workOrderReasonName;
    @ExcelProperty("处理人")
    private String processor;
    @ExcelProperty("服务商名称")
    private String serverName;
    @ExcelProperty("创建人")
    private String creater;
    @ExcelProperty("处理时间")
    private String processorTimeStr;
    @ExcelProperty("工单状态")
    private String statusStr;
    @ExcelProperty("工单编号")
    private String orderNo;
    @ExcelProperty("创建时间")
    private String createTimeStr;
}
