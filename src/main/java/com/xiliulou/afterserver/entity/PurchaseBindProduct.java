package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @program: XILIULOU
 * @description:  采购绑定点位表
 * @author: Mr.YG
 * @create: 2021-02-01 14:33
 **/
@Data
@TableName("purchase_bind_product")
public class PurchaseBindProduct {

    private Long productId;
    private Long purchaseId;
    private Integer count;
}
