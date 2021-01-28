package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.nio.file.LinkOption;

/**
 * @program: XILIULOU
 * @description: 采购
 * @author: Mr.YG
 * @create: 2021-01-28 17:59
 **/
@Data
@TableName("purchase")
public class Purchase {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private long customer;
    private String city;
    private Integer count;
    private BigDecimal fee;
    private String billsFees;
    private Long settingTime;
    private Long createTime;
}
