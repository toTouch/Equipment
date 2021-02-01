package com.xiliulou.afterserver.web.query;

import com.xiliulou.afterserver.entity.Purchase;
import com.xiliulou.afterserver.entity.PurchaseBindProduct;
import lombok.Data;

import java.util.List;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-01 14:32
 **/
@Data
public class SavePurchaseQuery {
    private Purchase purchase;
    private List<PurchaseBindProduct> purchaseBindProductList;
}
