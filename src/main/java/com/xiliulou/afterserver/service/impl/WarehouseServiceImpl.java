package com.xiliulou.afterserver.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

import java.util.Map;
import java.util.Objects;


@Service
@Slf4j
public class WarehouseServiceImpl extends ServiceImpl<WareHouseMapper, WareHouse> implements WarehouseService {

    @Override
    public IPage getPage(Long offset, Long size, WareHouse wareHouse){
        Page page = PageUtil.getPage(offset,size);

        LambdaQueryWrapper<WareHouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Objects.nonNull(wareHouse.getWareHouses()),WareHouse::getWareHouses,wareHouse.getWareHouses())
                .like(Objects.nonNull(wareHouse.getHead()),WareHouse::getHead,wareHouse.getHead())
                .like(Objects.nonNull(wareHouse.getAddress()),WareHouse::getAddress,wareHouse.getAddress())
                .like(Objects.nonNull(wareHouse.getPhone()),WareHouse::getPhone,wareHouse.getPhone())
                .orderByDesc(WareHouse::getCreateTime);
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public IPage getPageForList(Map<String, Object> map) {
        Long offset = (Long) map.get("offset");
        Long size = (Long) map.get("size");
        Page page = PageUtil.getPage(offset, size);
        QueryWrapper<WareHouse> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        return this.baseMapper.selectPage(page,wrapper);
    }
}
