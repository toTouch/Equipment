package com.xiliulou.afterserver.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.List;

/**
 * @author Hardy
 * @date 2021/8/26 15:01
 * @mood
 */
@Data
public class PointExcelVo {
    /**
     * 名称
     */
    @ExcelProperty("机柜名称")
    private String name;
    /**
     * 状态 1,移机,2运营中,3:拆机
     */
    @ExcelProperty("机柜状态")
    private String status;
    /**
     * 安装类型（1:室外 2:半室外3：室内）
     */
    @ExcelProperty("安装类型")
    private String installType;
    /**
     * 详细地址
     */
    @ExcelProperty("详细地址")
    private String address;
    /**
     * 摄像头数量
     */
    @ExcelProperty("摄像头数量")
    private Integer cameraCount;
    /**
     * 雨棚数量
     */
    @ExcelProperty("雨棚数量")
    private Integer canopyCount;
    /**
     * SN码
     */
    @ExcelProperty("SN码")
    private String snNo;
    /**
     * 物联网卡号
     */
    @ExcelProperty("物联网卡号")
    private String cardNumber;

    /**
     * 创建时间
     */
    @ExcelProperty("安装时间")
    private String createTime;

    @ExcelProperty("城市名称")
    private String cityName;

    @ExcelProperty("客户名称")
    private String customerName;


    @ExcelProperty("产品类型及数量")
    private String peoductNumAndType;
}
