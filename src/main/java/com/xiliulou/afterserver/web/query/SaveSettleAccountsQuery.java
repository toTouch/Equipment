package com.xiliulou.afterserver.web.query;

import com.xiliulou.afterserver.entity.SettleAccounts;
import lombok.Data;

import java.util.List;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-04 16:24
 **/
@Data
public class SaveSettleAccountsQuery {
    private Long settleAccountsId;

    List<PointBindSettleAccountsQuery> pointBindSettleAccountsQueryList;


}
