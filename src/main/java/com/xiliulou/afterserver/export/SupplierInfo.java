package com.xiliulou.afterserver.export;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @program: XILIULOU
 * @description: 供应商
 * @author: Mr.YG
 * @create: 2021-01-28 17:35
 **/
@Data
public class SupplierInfo {

    @ExcelProperty(index = 0)
    private String label;
    /**
     * 公司名称
     */
    @ExcelProperty(index = 3)
    private String companyName;
    @ExcelProperty(index = 4)
    private String manager;
    @ExcelProperty(index = 5)
    private String phone;
    @ExcelProperty(index = 2)
    private String area;
    //发票类型
    @ExcelProperty(index = 7)
    private String billType;
    //费用明细
    @ExcelProperty(index = 6)
    private String scheduleOfFees;
    /**
     * 城市id
     */
    @ExcelProperty(index = 1)
    private String city;

}
