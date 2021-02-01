package com.xiliulou.afterserver.web.query;

import com.xiliulou.afterserver.entity.Purchase;
import lombok.Data;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-01 17:03
 **/
@Data
public class PurchaseQuery extends Purchase {
    private Long settingTimeStart;
    private Long settingTimeEnd;
}
