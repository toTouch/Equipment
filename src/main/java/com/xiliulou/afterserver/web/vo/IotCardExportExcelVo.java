package com.xiliulou.afterserver.web.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author Hardy
 * @date 2022/2/14 11:19
 * @mood
 */
@Data
public class IotCardExportExcelVo {
    @ExcelProperty("物联网卡号")
    private String sn;
    @ExcelProperty("供应商")
    private String supplier;
    @ExcelProperty("运营商")
    private String operator;
    @ExcelProperty("批次号")
    private String batch;
    @ExcelProperty("激活时间")
    private String activationTime; //激活时间
    @ExcelProperty("套餐")
    private String packages;
    @ExcelProperty("有效时间")
    private String termOfAlidity; //有效时间
    @ExcelProperty("过期时间")
    private String expirationTime; //过期时间
    @ExcelProperty("创建时间")
    private String createTime;
}
