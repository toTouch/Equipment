package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.ProductIendingApplication;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.InventoryFlowBillQuery;
import com.xiliulou.afterserver.web.query.ProductLendingApplicationItemQuery;
import com.xiliulou.afterserver.web.query.ProductLendingApplicationQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zgw
 * @date 2021/9/18 15:44
 * @mood
 */
public interface ProductIendingApplicationService  extends IService<ProductIendingApplication> {
    IPage getPage(Long offset, Long size, ProductLendingApplicationQuery productLendingApplicationQuery);

    R insert(ProductLendingApplicationQuery productLendingApplicationQuery, List<ProductLendingApplicationItemQuery> list);

    R updateStatus(Long id, Long status);
}
