package com.xiliulou.afterserver.web.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-02 15:01
 **/
@Data
public class ProductSerialNumberExcelVo {
    @ExcelProperty("序列号")
    private String serialNumber;
    @ExcelProperty("产品型号")
    private String productName;
    @ExcelProperty("创建时间")
    private String createTimeStr;
}
