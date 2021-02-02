package com.xiliulou.afterserver.web.vo;

import com.xiliulou.afterserver.entity.SettleAccounts;
import lombok.Data;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-01 15:54
 **/
@Data
public class SettleAccountsVo extends SettleAccounts {
    private String pointName;
    private String customerName;
}
