package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 16:44
 **/
@Data
@TableName("point_bind_product")
public class PointBindProduct {

    private Long pointId;
    private Long productId;
}
