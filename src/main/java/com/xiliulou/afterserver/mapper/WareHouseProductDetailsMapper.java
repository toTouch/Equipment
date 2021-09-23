package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiliulou.afterserver.entity.WareHouseProductDetails;
import com.xiliulou.afterserver.web.query.WorkOrderQuery;
import com.xiliulou.afterserver.web.vo.WareHouseProductDetailsVo;
import com.xiliulou.afterserver.web.vo.WorkOrderVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author zgw
 * @date 2021/9/17 18:22
 * @mood
 */
@Mapper
public interface WareHouseProductDetailsMapper extends BaseMapper<WareHouseProductDetails> {
    Page<WareHouseProductDetailsVo> getPage(Page page, @Param("query") WareHouseProductDetails wareHouseProductDetails);
}
