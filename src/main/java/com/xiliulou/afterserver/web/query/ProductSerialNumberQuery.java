package com.xiliulou.afterserver.web.query;

import lombok.Data;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-02 14:16
 **/
@Data
public class ProductSerialNumberQuery {

    private String prefix;
    //左区间
    private Long leftInterval;
    //右区间
    private Long rightInterval;
    private Long productId;

    private Long createTimeStart;
    private Long createTimeEnd;

}
