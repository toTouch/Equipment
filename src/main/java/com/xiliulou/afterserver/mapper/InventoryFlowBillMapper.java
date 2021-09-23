package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiliulou.afterserver.entity.InventoryFlowBill;
import com.xiliulou.afterserver.entity.WareHouseProductDetails;
import com.xiliulou.afterserver.web.query.InventoryFlowBillQuery;
import com.xiliulou.afterserver.web.vo.InventoryFlowBillVo;
import com.xiliulou.afterserver.web.vo.WareHouseProductDetailsVo;
import org.apache.ibatis.annotations.Param;

/**
 * @author zgw
 * @date 2021/9/18 13:57
 * @mood
 */
public interface InventoryFlowBillMapper extends BaseMapper<InventoryFlowBill> {
    Page<InventoryFlowBillVo> getPage(Page page, @Param("query") InventoryFlowBillQuery inventoryFlowBillQuery);
}
