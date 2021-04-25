package com.xiliulou.afterserver.export;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author Hardy
 * @date 2021/4/25 0025 15:30
 * @Description:
 */
@Data
public class ServiceInfo {
    @ExcelProperty(index = 0)
    private String name;
    @ExcelProperty(index = 1)
    private String manager;
    @ExcelProperty(index = 2)
    private String phone;
    @ExcelProperty(index = 3)
    private String area;
    @ExcelProperty(index = 5)
    private String remark;
    @ExcelProperty(index = 4)
    private String scope;
}
