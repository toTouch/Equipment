package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiliulou.afterserver.entity.ProductIendingApplication;
import com.xiliulou.afterserver.entity.ProductLendingApplicationItem;
import com.xiliulou.afterserver.web.query.ProductLendingApplicationQuery;
import org.apache.ibatis.annotations.Param;

/**
 * @author Hardy
 * @date 2021/9/22 9:15
 * @mood
 */
public interface ProductIendingApplicationItemMapper extends BaseMapper<ProductLendingApplicationItem> {
    Page<ProductIendingApplication> getPage(Page page, @Param("productLendingAppId")Long productLendingAppId);
}
