package com.xiliulou.afterserver.web.vo;

import com.xiliulou.afterserver.entity.Product;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-01 11:23
 **/
@Data
public class PurchaseVo {

    private String city;
    private BigDecimal fee;
    private String billFees;
    private Long settingTime;
    private Long createTime;
    private String pointName;
    private String customerName;
    private List<ProductBindVo> productBindVoList;
}
