package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @program: XILIULOU
 * @description: 服务商表
 * @author: Mr.YG
 * @create: 2021-02-04 15:54
 **/
@Data
@TableName("server")
public class Server {
    private Long id;

    private String name;
    private String manager;
    private String phone;
    private String area;
    private Long createTime;
    private String remark;
    private String scope;
    /**
     * 满意度
     */
    private String satisfaction;
    /**
     * 合作状态 1：首次接单 2：试用期 3：初级合作 4：中极合作 5：高级合作 6：淘汰
     */
    private Integer status;
    /**
     * 创建人
     */
    private Long createUid;
    /**
     * 收货地址
     */
    private String address;

    @TableField(exist = false)
    private String createUname;
}
