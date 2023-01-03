package com.xiliulou.afterserver.export;

import com.alibaba.excel.annotation.ExcelProperty;
import java.math.BigDecimal;
import lombok.Data;

/**
 * @author zgw
 * @date 2022/12/27 15:43
 * @mood
 */
@Data
public class PartsInfo {
    @ExcelProperty(index = 0)
    private String sn;
    @ExcelProperty(index = 1)
    private String name;
    @ExcelProperty(index = 2)
    private BigDecimal purchasePrice;
    @ExcelProperty(index = 3)
    private BigDecimal sellPrice;
}
