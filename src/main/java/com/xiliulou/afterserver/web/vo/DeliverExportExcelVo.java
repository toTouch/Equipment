package com.xiliulou.afterserver.web.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Hardy
 * @date 2021/9/8 15:27
 * @mood
 */
@Data
public class DeliverExportExcelVo {

   @ExcelProperty("客户")
    private String customerName;
   @ExcelProperty("客户电话")
   private Long phone;

    @ExcelProperty("起点")
    private String city;

    @ExcelProperty("终点")
    private String destination;
    /**
     * 物流状态 1：未发货  2：已发货  3：已到达
     */
    @ExcelProperty("物流状态")
    private String stateStr;
    @ExcelProperty("第三方公司")
    private String thirdCompanyName; //set方法
    @ExcelProperty("第三方承担费用（元）")
    private BigDecimal thirdCompanyPay;
    @ExcelProperty("运费")
    private BigDecimal deliverCost;
    @ExcelProperty("发货时间")
    private String deliverTime; //set
    @ExcelProperty("快递公司")
    private String expressCompany;
    @ExcelProperty("快递单号")
    private String expressNo;
    @ExcelProperty("创建人")
    private String createUName; //set方法
    @ExcelProperty("备注")
    private String remark;
}
