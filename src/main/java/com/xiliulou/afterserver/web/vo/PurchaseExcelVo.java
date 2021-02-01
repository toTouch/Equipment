package com.xiliulou.afterserver.web.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-01 11:00
 **/
@Data
public class PurchaseExcelVo {

    @ExcelProperty("客户名称")
    private String customerName;
    @ExcelProperty("点位名称")
    private String pointName;
    @ExcelProperty("城市")
    private String city;
    @ExcelProperty("费用")
    private BigDecimal fee;
    @ExcelProperty("费用明细")
    private String billsFees;
    @ExcelProperty("安装时间")
    private String settingTimeStr;
    @ExcelProperty("创建时间")
    private String createTimeStr;
}
