package com.xiliulou.afterserver.web.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-01 10:32
 **/
@Data
public class ProductExcelVo {
    
    // 产品系列
    // 取餐柜 1、餐厅柜 2、换电柜 3、充电柜 4、寄存柜 5、生鲜柜 6
    @ExcelProperty("产品型号")
    private String productSeries;
    
    /**
     * 购买类型 1--集采 2--非集采
     */
    @ExcelProperty("购买类型")
    private String buyType;
    
    @ExcelProperty("型号名称")
    private String name;
    
    @ExcelProperty("格口数量")
    private Integer boxNumber;
    
    /**
     * 是否有屏  0无 1有
     */
    @ExcelProperty("柜机类型")
    private String hasScreen;
    
    /**
     * 消防类型 0-水消防 1-气溶胶消防
     */
    @ExcelProperty("消防类型")
    private String fireFightingType;
    
    @ExcelProperty("型号编码")
    private String code;
    
    @ExcelProperty("备注")
    private String remarks;
}
