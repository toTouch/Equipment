package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @program: XILIULOU
 * @description: 供应商
 * @author: Mr.YG
 * @create: 2021-01-28 17:35
 **/
@Data
@TableName("supplier")
public class Supplier {
    private Long id;
    private String label;
    private String name;
    private String manager;
    private String phone;
    private String area;
    private Integer province;
    private Long createTime;
    //发票类型
    private String billType;
    //费用明细
    private String scheduleOfFees;
    private String code;

    @TableField(exist = false)
    private String cityName;
    /**
     * 代码
     */
    private String simpleName;
}
