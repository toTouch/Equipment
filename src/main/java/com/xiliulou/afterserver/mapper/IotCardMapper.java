package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.InventoryFlowBill;
import com.xiliulou.afterserver.entity.IotCard;

public interface IotCardMapper extends BaseMapper<IotCard> {

    Integer saveOne(IotCard iotCard);

    Integer updateOne(IotCard iotCard);
}
