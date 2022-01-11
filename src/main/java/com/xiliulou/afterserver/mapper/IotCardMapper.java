package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiliulou.afterserver.entity.InventoryFlowBill;
import com.xiliulou.afterserver.entity.IotCard;

public interface IotCardMapper extends BaseMapper<IotCard> {

    Integer saveOne(IotCard iotCard);

    Integer updateOne(IotCard iotCard);

    IPage<IotCard> getPage(Page page, IotCard iotCard);
}
