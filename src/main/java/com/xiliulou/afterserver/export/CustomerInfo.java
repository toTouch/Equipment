package com.xiliulou.afterserver.export;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author Hardy
 * @date 2021/4/25 0025 13:41
 * @Description:
 */
@Data
public class CustomerInfo {

    @ExcelProperty(index = 0)
    private String label;

    @ExcelProperty(index = 1)
    private String name;

    @ExcelProperty(index = 2)
    private String manager;

    @ExcelProperty(index = 3)
    private String phone;




}
