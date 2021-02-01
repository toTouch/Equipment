package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @program: XILIULOU
 * @description: 用户
 * @author: Mr.YG
 * @create: 2021-01-28 16:56
 **/
@TableName("user")
@Data
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private String name;
    private String password;
    private Integer roleId;
    private Long createTime;

    public static final Integer AFTER_USER_ROLE = 1;


}
