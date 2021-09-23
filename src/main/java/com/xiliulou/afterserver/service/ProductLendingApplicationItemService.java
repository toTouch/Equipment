package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.ProductIendingApplication;
import com.xiliulou.afterserver.entity.ProductLendingApplicationItem;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ProductLendingApplicationQuery;

/**
 * @author Hardy
 * @date 2021/9/22 9:11
 * @mood
 */
public interface ProductLendingApplicationItemService extends IService<ProductLendingApplicationItem> {
    IPage getPage(Long offset, Long size, Long productLendingAppId);
    R edit(Long id, Long takeNum, Long returnNum);
}
