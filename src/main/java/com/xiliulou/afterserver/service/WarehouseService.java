package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.WareHouse;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.PointQuery;
import com.xiliulou.afterserver.web.query.WareHouseQuery;

import java.util.Map;

public interface WarehouseService extends IService<WareHouse> {

    IPage getPage(Long offset, Long size, WareHouse warehouse);


    IPage getPageForList(Map<String, Object> map);

    R pointBindSerialNumber(WareHouseQuery wareHouseQuery);

    R queryWarehousePull();
}
