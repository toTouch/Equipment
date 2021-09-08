package com.xiliulou.afterserver.web.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.xiliulou.afterserver.entity.File;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Hardy
 * @date 2021/9/8 10:11
 * @mood
 */
@Data
public class WorkOrderListExcelVo {
    @ExcelProperty("工单类型")
    private String typeName;

    @ExcelProperty("点位")
    private String pointName;

    @ExcelProperty("sn码")
    private String snNo;

    @ExcelProperty("费用")
    private BigDecimal fee;

    @ExcelProperty("第三方公司")
    private String thirdCompanyName;

    @ExcelProperty("第三方承担费用（元）")
    private BigDecimal thirdCompanyPay;

    @ExcelProperty("工单原因")
    private String workOrderReasonName;

    @ExcelProperty("创建人")
    private String createrName;

    @ExcelProperty("处理时间")
    private String processTime;

    @ExcelProperty("状态")
    private String statusName;

    @ExcelProperty("工单编号")
    private String orderNo;

    @ExcelProperty("备注")
    private String info;

}
