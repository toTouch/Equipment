package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @program: XILIULOU
 * @description: 工单类型
 * @author: Mr.YG
 * @create: 2021-01-29 10:40
 **/
@Data
@TableName("work_order_type")
public class WorkOrderType {

    private Long id;
    private String type;
    private String remark;
    private String no;


    //安装
    public static final Integer INSTALLATION = 1;
    //派送
//    public static final Integer DELIVER = 2;
    //接电
    public static final Integer CONNECTION_ELECTRICITY = 3;
    //移机
    public static final Integer TRANSFER = 4;
    //售后
    public static final Integer AFTER_SALE = 5;
    //物流
    public static final Integer LOGISTICS = 6;
}
