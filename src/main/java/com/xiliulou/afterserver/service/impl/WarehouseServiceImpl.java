package com.xiliulou.afterserver.service.impl;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.PointNew;
import com.xiliulou.afterserver.entity.PointProductBind;
import com.xiliulou.afterserver.entity.ProductNew;
import com.xiliulou.afterserver.entity.WareHouse;
import com.xiliulou.afterserver.mapper.PointProductBindMapper;
import com.xiliulou.afterserver.mapper.WareHouseMapper;
import com.xiliulou.afterserver.service.ProductNewService;
import com.xiliulou.afterserver.service.WarehouseService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.PointQuery;
import com.xiliulou.afterserver.web.query.WareHouseQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;


@Service
@Slf4j
public class WarehouseServiceImpl extends ServiceImpl<WareHouseMapper, WareHouse> implements WarehouseService {

    @Autowired
    PointProductBindMapper pointProductBindMapper;
    @Autowired
    ProductNewService productNewService;

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

    @Override
    public R pointBindSerialNumber(WareHouseQuery wareHouseQuery) {
        WareHouse wareHouse = getById(wareHouseQuery.getId());
        if (Objects.isNull(wareHouse)) {
            return R.failMsg("未找到仓库信息!");
        }
        if (ObjectUtil.isNotEmpty(wareHouseQuery.getProductSerialNumberIdAndSetNoMap())) {
            wareHouseQuery.getProductSerialNumberIdAndSetNoMap().forEach((k,v) -> {
                PointProductBind pointProductBind = pointProductBindMapper
                        .selectOne(new QueryWrapper<PointProductBind>()
                                .eq("product_id", k));

                if(ObjectUtils.isNotNull(pointProductBind)){
                    ProductNew productNew = productNewService.queryByIdFromDB(k);
                    R.fail("柜机 +【" + (productNew == null?"未知":productNew.getNo() )+ "】柜机已绑定");
                }
            });
        }



        if (ObjectUtil.isNotEmpty(wareHouseQuery.getProductSerialNumberIdAndSetNoMap())) {
            wareHouseQuery.getProductSerialNumberIdAndSetNoMap().forEach((k,v) -> {
                PointProductBind bind = new PointProductBind();
                bind.setPointId(new Long(wareHouseQuery.getId()));
                bind.setProductId(k);
                bind.setPointType(PointProductBind.TYPE_WAREHOUSE);
                pointProductBindMapper.insert(bind);
            });
        }
        return R.ok();
    }

    @Override
    public R queryWarehousePull() {
        return R.ok(baseMapper.queryWarehousePull());
    }

    @Override
    public R queryWarehouseLikeNamePull(String name) {
        return R.ok(baseMapper.queryWarehouseLikeNamePull(name));
    }
}
