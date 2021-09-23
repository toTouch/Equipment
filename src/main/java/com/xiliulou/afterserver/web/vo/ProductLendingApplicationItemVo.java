package com.xiliulou.afterserver.web.vo;

import lombok.Data;

/**
 * @author Hardy
 * @date 2021/9/22 11:18
 * @mood
 */
@Data
public class ProductLendingApplicationItemVo {
    private Long id;

    private Long applyNum;

    private Long takeNum;

    private Long returnNum;

    private Long productId;

    private Long productLendingAppId;

    private String productName;

    private String productNo;
}
