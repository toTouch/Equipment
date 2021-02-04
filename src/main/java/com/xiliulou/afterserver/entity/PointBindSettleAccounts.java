package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-04 16:31
 **/
@Data
@TableName("point_bind_settle_accounts")
public class PointBindSettleAccounts {
    private Long pointId;
    private Long settleAccountsId;
    private BigDecimal deviceAmount;
    private BigDecimal serverAmount;
    private Long createTime;

}
