package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiliulou.afterserver.entity.ProductIendingApplication;
import com.xiliulou.afterserver.web.query.ProductLendingApplicationQuery;
import com.xiliulou.afterserver.web.vo.InventoryFlowBillVo;
import com.xiliulou.afterserver.web.vo.ProductIendingApplicationVo;
import org.apache.ibatis.annotations.Param;

/**
 * @author Hardy
 * @date 2021/9/18 15:42
 * @mood
 */
public interface ProductIendingApplicationMapper extends BaseMapper<ProductIendingApplication> {
    Page<ProductIendingApplication> getPage(Page page, @Param("query") ProductLendingApplicationQuery productLendingApplicationQuery);
}
