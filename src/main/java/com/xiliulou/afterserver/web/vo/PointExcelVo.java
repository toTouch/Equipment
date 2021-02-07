package com.xiliulou.afterserver.web.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-07 17:32
 **/
@Data
public class PointExcelVo {
    @ExcelProperty("城市")
    private String city;
    @ExcelProperty("点位名称")
    private String pointName;
    @ExcelProperty("客户")
    private String customerName;
    @ExcelProperty("主柜数量")
    private String primaryCabinetCount;
    @ExcelProperty("副柜数量")
    private String deputyCabinetCount;
    @ExcelProperty("总金额")
    private String allAmount;
}
