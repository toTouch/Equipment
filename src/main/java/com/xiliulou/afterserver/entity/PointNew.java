package com.xiliulou.afterserver.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.List;

/**
 * (PointNew)实体类
 *
 * @author Eclair
 * @since 2021-08-17 10:28:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_point_new")
public class PointNew {

    private Long id;
    /**
     * 名称
     */
    private String name;
    /**
     * 客户id
     */
    private Long customerId;
    /**
     * 状态 1,正常2,移机,3:拆机
     */
    private Integer status;
    /**
     * 城市id
     */
    private Integer cityId;
    /**
     * 安装类型（1:室外 2:半室外3：室内）
     */
    private Integer installType;
    /**
     * 详细地址
     */
    private String address;
    /**
     * 摄像头数量
     */
    private Integer cameraCount;
    /**
     * 雨棚数量
     */
    private Integer canopyCount;
    /**
     * SN码
     */
    private String snNo;
    /**
     * 物联网卡号
     */
    private String cardNumber;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 删除状态 0正常 1 删除
     */
    private Integer delFlag;

    public static final Integer DEL_NORMAL = 0;
    public static final Integer DEL_DEL = 1;

    @TableField(exist = false)
    private List<Long> productIds;
    @TableField(exist = false)
    private String cityName;
    @TableField(exist = false)
    private String customerName;
}
