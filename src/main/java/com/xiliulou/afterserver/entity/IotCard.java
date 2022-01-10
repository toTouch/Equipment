package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_iot_card")
public class IotCard {
    private Long id;
    private Long batchId;
    private Long supplierId;
    private Integer operator;
    private Long activationTime; //激活时间
    private String packages;
    private Long termOfAlidity; //有效时间
    private Long expirationTime; //过期时间
    private Long createTime;
    private Long updateTime;
    private Integer delFlag;

    public static final Integer OPERATOR_MOVE = 1; //中国移动
    public static final Integer OPERATOR_TELECOM = 2; //中国电信
    public static final Integer OPERATOR_UNICOM = 3; //中国联通

    public static final Integer DEL_NORMAL = 0;
    public static final Integer DEL_DEL = 1;
}
