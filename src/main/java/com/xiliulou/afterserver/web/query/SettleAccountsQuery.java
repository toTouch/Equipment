package com.xiliulou.afterserver.web.query;

import com.xiliulou.afterserver.entity.SettleAccounts;
import lombok.Data;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-01 15:43
 **/
@Data
public class SettleAccountsQuery extends SettleAccounts {
    private Long pointId;
    private Long payTimeStart;
    private Long payTimeEnd;

}
