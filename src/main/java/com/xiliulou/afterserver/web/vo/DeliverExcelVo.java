package com.xiliulou.afterserver.web.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class DeliverExcelVo {

    @ExcelProperty("客户")
    private Long customerId;
    @ExcelProperty("客户电话")
    private Long phone;//客户电话
    @ExcelProperty("起点")
    private String province;
    @ExcelProperty("终点")
    private String destination;//终点
    @ExcelProperty("账单")
    private String bills;
    @ExcelProperty("配送费用")
    private BigDecimal deliverCost;
    @ExcelProperty("配送时间")
    private String deliverTime;
    @ExcelProperty("创建时间")
    private String createTime;
    @ExcelProperty("快递公司")
    private String expressCompany;
    @ExcelProperty("快递单号")
    private String expressNo;
    @ExcelProperty("产品类型")
    private String type;//产品型号
    @ExcelProperty("物流状态")
    private String state;//物流状态
    @ExcelProperty("数量")
    private String quantity;//数量
}
