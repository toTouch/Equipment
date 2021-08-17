package com.xiliulou.afterserver.export;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Hardy
 * @date 2021/6/10 0010 17:55
 * @Description:
 */
@Data
public class PointInfo {
    /**
     * 名称
     */
    @ExcelProperty(index = 0)
    private String name;
    /**
     * 客户
     */
    @ExcelProperty(index = 1)
    private String customerId;

    /**
     * 点位状态
     */
    @ExcelProperty(index = 2)
    private Integer status;

    /**
     * 摄像头数量
     */
    @ExcelProperty(index = 3)
    private BigDecimal cameraCount;

    /**
     * 雨棚数量
     */
    @ExcelProperty(index = 4)
    private Integer canopyCount;

    /**
     * 城市
     */
    @ExcelProperty(index = 5)
    private String city;

    /**
     *sn码
     */
    @ExcelProperty(index = 6)
    private String snNo;
    /**
     * 物联网卡
     */
    @ExcelProperty(index = 7)
    private String cardNumber;

}
