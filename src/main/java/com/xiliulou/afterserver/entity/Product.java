package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @program: XILIULOU
 * @description: 产品
 *
 * @author: Mr.YG
 * @create: 2021-01-28 17:03
 **/
@Data
@TableName("product")
public class Product {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;
    private Integer type;
    private String code;
    private Integer boxNumber;
    private Long createTime;
    private String info;
}
