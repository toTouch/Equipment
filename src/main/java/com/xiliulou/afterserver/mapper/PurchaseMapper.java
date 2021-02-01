package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiliulou.afterserver.entity.Purchase;
import com.xiliulou.afterserver.web.query.PurchaseQuery;
import com.xiliulou.afterserver.web.vo.PurchaseVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PurchaseMapper extends BaseMapper<Purchase> {
    IPage getPage(Page page, @Param("query") PurchaseQuery purchase);

    List<PurchaseVo> getList(@Param("query") PurchaseQuery purchase);
}
