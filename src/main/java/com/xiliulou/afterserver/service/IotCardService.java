package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.IotCard;
import com.xiliulou.afterserver.util.R;

public interface IotCardService extends IService<IotCard> {

    R saveOne(IotCard iotCard);

    R updateOne(IotCard iotCard);

    R deleteOne(Long id);

    R getPage(Long offset, Long size, IotCard iotCard);
}
