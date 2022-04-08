package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.City;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.mapper.SupplierMapper;
import com.xiliulou.afterserver.service.CityService;
import com.xiliulou.afterserver.service.SupplierService;
import com.xiliulou.afterserver.service.UserService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.web.query.FactoryUserInfoVo;
import com.xiliulou.afterserver.web.vo.SupplierPullVo;
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
    @Autowired
    UserService userService;
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

    @Override
    public Supplier querySupplierName(String supplierName) {
        return baseMapper.selectOne(new QueryWrapper<Supplier>().eq("name", supplierName));
    }

    @Override
    public R getUserInfo() {
        Long uid = SecurityUtils.getUid();
        User user = userService.getUserById(uid);
        if(Objects.isNull(user)){
            return R.fail(null, "未查询到相关用户信息");
        }

        Supplier supplier = this.getById(user.getThirdId());
        if(Objects.isNull(supplier)){
            return R.fail(null, "未查询到相关工厂信息");
        }

        FactoryUserInfoVo vo = new FactoryUserInfoVo();
        vo.setSupplierName(supplier.getName());
        return R.ok(vo);
    }

    @Override
    public List<SupplierPullVo> querySupplierPull(String name) {
        return this.baseMapper.querySupplierPull(name);
    }
}
