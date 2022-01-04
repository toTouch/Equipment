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
     * 名称
     */
    @ExcelProperty(index = 1)
    private String name;
    /**
     * 客户
     */
    @ExcelProperty(index = 2)
    private String customer;

    /**
     * 点位状态
     */
    @ExcelProperty(index = 3)
    private String status;
    /**
     * 城市
     */
    @ExcelProperty(index = 4)
    private String city;
    /**
     * 安装类型
     */
    @ExcelProperty(index = 5)
    private String installType;
   /**
     * 详细地址
     */
    @ExcelProperty(index = 6)
    private String address;
    /**
     * 摄像头数量
     */
    @ExcelProperty(index = 7)
    private Integer cameraCount;

    /**
     * 雨棚数量
     */
    @ExcelProperty(index = 8)
    private Integer canopyCount;
     /**
     *sn码
     */
    @ExcelProperty(index = 9)
    private String snNo;

    /**
     * 物联网卡
     */
    @ExcelProperty(index = 10)
    private String cardNumber;
    /**
     * '物联网卡供应商
     */
    @ExcelProperty(index = 11)
    private String cardSupplier;

    /**
     * 安装时间
     */
    @ExcelProperty(index = 12)
    private String installTime;
     @ExcelProperty(index = 13)
    private String delFlag;
    /**
     * 备注
     */
    @ExcelProperty(index = 14)
    private String remarks;
    /**
     * 创建人id
     */
    @ExcelProperty(index = 15)
    private String createUid;



    /**
     * 施工完成时间
     */
    @ExcelProperty(index = 16)
    private String completionTime;

    /**
     * 入账
     */
    @ExcelProperty(index = 17)
    private String isEntry;
    /**
     * 验收
     */
    @ExcelProperty(index = 18)
    private String isAcceptance;
    /**
     * 订单来源
     */
    @ExcelProperty(index = 19)
    private String orderSource;
    /**
     * 下单时间
     */
    @ExcelProperty(index = 20)
    private String orderTime;
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
}
