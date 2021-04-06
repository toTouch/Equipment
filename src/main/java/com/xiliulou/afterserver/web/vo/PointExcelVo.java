package com.xiliulou.afterserver.web.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-07 17:32
 **/
@Data
public class PointExcelVo {
    @ExcelProperty("名称")
    private String name;
    @ExcelProperty("客户")
    private String customerName;
    @ExcelProperty("点位状态")
    private Integer status;
    @ExcelProperty("已支付费用")
    private BigDecimal paiedAmount;
    @ExcelProperty("应收费用")
    private BigDecimal serverAmount;
    @ExcelProperty("主柜数量")
    private String primaryCabinetCount;
    @ExcelProperty("副柜数量")
    private String deputyCabinetCount;
    @ExcelProperty("雨棚数量")
    private Integer canopyCount;
    @ExcelProperty("城市")
    private String city;
    @ExcelProperty("序列号")
    private Integer primaryCabinetNo;
    @ExcelProperty("sn码")
    private String serialNumber;
    @ExcelProperty("物联网卡号")
    private String cardNo;
    @ExcelProperty("备注")
    private String remark;
    @ExcelProperty("安装时间")
    private Long setTime;
    @ExcelProperty("发货时间")
    private Long deliverTime;
}
