package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ware_house")
public class WareHouse implements Serializable {

    private static final long serialVersionUID = 1L;

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
