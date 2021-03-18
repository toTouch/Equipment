package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ware_house")
public class WareHouse {
    private Integer id;
    private String wareHouses;
    private String address;
    private String head;
    private String phone;
    private Long createTime;
    @TableField(exist = false)
    private String scope;//服务范围
    @TableField(exist = false)
    private String remark;//备注
}
