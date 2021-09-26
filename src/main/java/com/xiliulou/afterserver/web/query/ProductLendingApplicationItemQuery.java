package com.xiliulou.afterserver.web.query;

import lombok.Data;

/**
 * @author zgw
 * @date 2021/9/18 17:50
 * @mood
 */
@Data
public class ProductLendingApplicationItemQuery {
    private Long id;

    private Long applyNum;

    private Long takeNum;

    private Long returnNum;

    private Long productId;

    private Long productLendingAppId;
}
