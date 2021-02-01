package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.Deliver;
import com.xiliulou.afterserver.web.query.DeliverQuery;

public interface DeliverService extends IService<Deliver> {
    IPage getPage(Long offset, Long size, DeliverQuery deliver);
}
