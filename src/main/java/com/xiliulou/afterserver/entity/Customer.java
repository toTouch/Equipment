package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 16:57
 **/
@TableName("customer")
@Data
public class Customer {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String label;
    private String name;
    private String manager;
    private String phone;
    private String area;
    private Long createTime;

}
