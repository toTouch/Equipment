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
    @ExcelProperty("审核状态")
    private String auditStatus;

    @ExcelProperty("工单类型")
    private String typeName;

    @ExcelProperty("点位")
    private String pointName;

    @ExcelProperty("创建人")
    private String createrName;

    @ExcelProperty("状态")
    private String statusName;

    @ExcelProperty("时效")
    private String prescription;

    @ExcelProperty("描述")
    private String describeinfo;

    @ExcelProperty("备注")
    private String info;

    @ExcelProperty("工单原因")
    private String workOrderReasonName;

    @ExcelProperty("第三方原因")
    private String thirdReason;

    @ExcelProperty("第三方公司")
    private String thirdCompanyName;

    @ExcelProperty("第三方费用")
    private BigDecimal thirdCompanyPay;

    @ExcelProperty("费用")
    private BigDecimal fee;

    @ExcelProperty("照片数量")
    private Integer fileCount;

    @ExcelProperty("处理时间")
    private String processTime;

    @ExcelProperty("创建时间")
    private String createTime;

    @ExcelProperty("服务商")
    private String serverName;

    @ExcelProperty("结算方式")
    private String paymentMethodName;

    @ExcelProperty("第三方责任对接人")
    private String thirdResponsiblePerson;

    @ExcelProperty("工单编号")
    private String orderNo;

    @ExcelProperty("第三方结算状态")
    private String thirdPaymentStatus;

    @ExcelProperty("sn码")
    private String snNo;

    @ExcelProperty("审核内容")
    private String auditRemarks;

}
