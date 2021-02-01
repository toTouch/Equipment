package com.xiliulou.afterserver.web.vo;

import com.xiliulou.afterserver.entity.Deliver;
import lombok.Data;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-01 16:24
 **/
@Data
public class DeliverVo extends Deliver {
    private String pointName;
    private String customerName;
}
