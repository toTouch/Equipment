package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

/**
 * @program: XILIULOU
 * @description: 用户
 * @author: Mr.YG
 * @create: 2021-01-28 16:56
 **/
@TableName("user")
@Data
public class User {
    private Long id;
    private String userName;
    private String passWord;
    private Integer roleId;
    private Long createTime;
    private Integer userType;
    //private Long supplierId;
    private Long thirdId;
    private String picture;

    public static final Integer AFTER_USER_ROLE = 1;
    //public static final Integer TYPE_USER_SUPER = 0;

    public static final Integer TYPE_SUPPER_ADMIN = 1;
    public static final Integer TYPE_AFTER_SALE = 2;
    public static final Integer TYPE_PATROL_APPLET = 3;
    public static final Integer TYPE_FACTORY = 4;

    @TableField(exist = false)
    private List<Long> rids;
}
