package com.xiliulou.afterserver.entity;

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


}
