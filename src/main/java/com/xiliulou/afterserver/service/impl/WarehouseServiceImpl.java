package com.xiliulou.afterserver.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.WareHouse;
import com.xiliulou.afterserver.mapper.WareHouseMapper;
import com.xiliulou.afterserver.service.WarehouseService;
import com.xiliulou.afterserver.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class WarehouseServiceImpl extends ServiceImpl<WareHouseMapper, WareHouse> implements WarehouseService {

    @Override
    public IPage getPage(Long offset, Long size, WareHouse wareHouse){
        Page page = PageUtil.getPage(offset,size);
        return baseMapper.selectPage(page, Wrappers.lambdaQuery(wareHouse).orderByDesc(WareHouse::getCreateTime));
    }
}
