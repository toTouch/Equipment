package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.WareHouse;
import org.springframework.stereotype.Service;

@Service

public interface WarehouseService extends IService<WareHouse> {

    IPage getPage(Long offset, Long size, WareHouse warehouse);


}
