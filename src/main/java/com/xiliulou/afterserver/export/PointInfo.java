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
     * //已支付费用
     */
    @ExcelProperty(index = 3)
    private BigDecimal paiedAmount;

    /**
     * //点位费
     */
    @ExcelProperty(index = 4)
    private BigDecimal serverAmount;

    /**
     * 主柜台数
     */
    @ExcelProperty(index = 5)
    private Integer primaryCabinetCount;

    /**
     * 副柜台数
     */
    @ExcelProperty(index = 5)
    private Integer deputyCabinetCount;

    /**
     * 雨棚数量
     */
    @ExcelProperty(index = 6)
    private Integer canopyCount;
    /**
     * 格口数量
     */
    @ExcelProperty(index = 7)
    private Integer boxNumber;

    /**
     * 柜子类型  1:常温柜 2：加热柜
     */
    @ExcelProperty(index = 8)
    private String lockerType;
    /**
     * 城市
     */
    @ExcelProperty(index = 9)
    private String city;
    /**
     * 序列号
     */
    @ExcelProperty(index = 10)
    private Integer primaryCabinetNo;
    /**
     *sn码
     */
    @ExcelProperty(index = 11)
    private String serialNumber;
    /**
     * //物联网卡
     */
    @ExcelProperty(index = 12)
    private String cardNo;
    /**
     * 备注
     */
    @ExcelProperty(index = 13)
    private String remark;
    /**
     * 年份
     */
    @ExcelProperty(index = 14)
    private Long createTime;


    /**
     * 安装日期
     */
    @ExcelProperty(index = 15)
    private String setTime;

    /**
     * 发货日期
     */
    @ExcelProperty(index = 16)
    private Long deliverTime;






}
