package com.xiliulou.afterserver.web.query;

import com.xiliulou.afterserver.entity.Deliver;
import lombok.Data;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-01 16:14
 **/
@Data
public class DeliverQuery extends Deliver {
    private Long deliverTimeStart;
    private Long deliverTimeEnd;


}
