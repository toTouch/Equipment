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
public class PointUpdateInfo {
    @ExcelProperty(index = 0)
    private Long id;
    /**
     * 产品系列
     */
    @ExcelProperty(index = 1)
    private String productSeries;
    /**
     * 城市
     */
    @ExcelProperty(index = 2)
    private String city;
    /**
     * 客户
     */
    @ExcelProperty(index = 3)
    private String customer;
    /**
     * 名称
     */
    @ExcelProperty(index = 4)
    private String name;
    /**
     * 点位状态
     */
    @ExcelProperty(index = 5)
    private String status;
    /**
     * 创建人id
     */
    @ExcelProperty(index = 6)
    private String createUid;
    /**
     * 创建时间
     */
    @ExcelProperty(index = 7)
    private String createTime;
    /**
     * 安装类型
     */
    @ExcelProperty(index = 8)
    private String installType;
    /**
     * 雨棚数量
     */
    @ExcelProperty(index = 9)
    private Integer canopyCount;
    /**
     *sn码
     */
    @ExcelProperty(index = 10)
    private String snNo;
    /**
     * 物联网卡
     */
    @ExcelProperty(index = 11)
    private String cardNumber;
    /**
     * '物联网卡供应商
     */
    @ExcelProperty(index = 12)
    private String cardSupplier;
    /**
     * 摄像头数量
     */
    @ExcelProperty(index = 13)
    private Integer cameraCount;
    /**
     * 详细地址
     */
    @ExcelProperty(index = 14)
    private String address;
    /**
     * 安装时间
     */
    @ExcelProperty(index = 15)
    private String installTime;
    /**
     * 质保有效期
     */
    @ExcelProperty(index = 16)
    private String warrantyPeriod;
    /**
     * 施工完成时间
     */
    @ExcelProperty(index = 17)
    private String completionTime;
    /**
     * 下单时间
     */
    @ExcelProperty(index = 18)
    private String orderTime;

    /**
     * 入账
     */
    @ExcelProperty(index = 19)
    private String isEntry;
    /**
     * 验收
     */
    @ExcelProperty(index = 20)
    private String isAcceptance;

    /**
     * '运营商'
     */
    @ExcelProperty(index = 21)
    private String operator;
    /**
     * '物流信息'
     */
    @ExcelProperty(index = 22)
    private String logisticsInfo;
    /**
     * 备注
     */
    @ExcelProperty(index = 23)
    private String remarks;

    @ExcelProperty(index = 24)
    private String delFlag;
}
