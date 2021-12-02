package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.City;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.mapper.SupplierMapper;
import com.xiliulou.afterserver.service.CityService;
import com.xiliulou.afterserver.service.SupplierService;
import com.xiliulou.afterserver.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 19:10
 **/
@Service
@Slf4j
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements SupplierService {

    @Autowired
    CityService cityService;

    @Override
    public IPage getPage(Long offset, Long size, Supplier supplier) {
        Page page = PageUtil.getPage(offset, size);

        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Objects.nonNull(supplier.getName()),Supplier::getName,supplier.getName())
                .like(Objects.nonNull(supplier.getManager()),Supplier::getManager,supplier.getManager())
                .like(Objects.nonNull(supplier.getPhone()),Supplier::getPhone,supplier.getPhone())
                .like(Objects.nonNull(supplier.getCode()),Supplier::getCode,supplier.getCode())
                .orderByDesc(Supplier::getCreateTime);

        Page page1 = baseMapper.selectPage(page,wrapper);
        List<Supplier> records = page1.getRecords();

        records.forEach(item -> {
            if (item.getArea()!=null){
                City city = cityService.queryByIdFromDB(Integer.parseInt(item.getArea()));
                if (Objects.nonNull(city)){
                    item.setCityName(city.getName());
                }
            }
        });
        page1.setRecords(records);
        return page1;
    }
}
