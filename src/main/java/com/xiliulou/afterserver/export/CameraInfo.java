package com.xiliulou.afterserver.export;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author Hardy
 * @date 2022/2/8 17:32
 * @mood
 */
@Data
public class CameraInfo {
    @ExcelProperty(index = 0)
    private String serialNum;
    @ExcelProperty(index = 1)
    private String supplier;
    @ExcelProperty(index = 2)
    private String cameraCard;
}
