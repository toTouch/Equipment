package com.xiliulou.afterserver.web.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-01 10:32
 **/
@Data
public class ProductExcelVo {

    @ExcelProperty("型号名称")
    private String name;
    @ExcelProperty("种类")
    private Integer type;
    @ExcelProperty("价格")
    private BigDecimal price;
    @ExcelProperty("型号编码")
    private String code;
    @ExcelProperty("格子数量")
    private Integer boxNumber;
    @ExcelProperty("创建时间")
    private Long createTime;
    @ExcelProperty("产品状态")
    private String state;

}
