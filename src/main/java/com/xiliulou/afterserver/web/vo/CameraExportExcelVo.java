package com.xiliulou.afterserver.web.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author Hardy
 * @date 2022/2/14 14:31
 * @mood
 */
@Data
public class CameraExportExcelVo {
    @ExcelProperty("序列号")
    private String serialNum;
    @ExcelProperty("厂家")
    private String supplier;
    @ExcelProperty("物联网卡号")
    private String cameraCard;
    @ExcelProperty("创建时间")
    private String createTime;
}
