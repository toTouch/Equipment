package com.xiliulou.afterserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.FactoryTestConf;
import com.xiliulou.afterserver.mapper.FactoryTestConfMapper;
import com.xiliulou.afterserver.service.FactoryTestConfService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ApiRequestQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @Author lny
 * @date 2023/8/18 14:26
 * @Description:
 **/
@Slf4j
@Service
public class FactoryTestConfServiceImpl extends ServiceImpl<FactoryTestConfMapper, FactoryTestConf> implements FactoryTestConfService {
    
    @Resource
    private FactoryTestConfMapper factoryTestConfMapper;
    
    @Override
    public List<FactoryTestConf> getAllByApi(ApiRequestQuery apiRequestQuery) {
        FactoryTestConf condition = new FactoryTestConf();
        if (ObjectUtils.isNotEmpty(apiRequestQuery) && ObjectUtils.isNotEmpty(apiRequestQuery.getData())) {
            FactoryTestConf conf = JSON.parseObject(apiRequestQuery.getData(), FactoryTestConf.class);
            BeanUtils.copyProperties(conf, condition);
        }
        return factoryTestConfMapper.queryAll(condition);
    }
    
    
    @Override
    public R getPage(Long offset, Long size, String confName) {
        Page page = PageUtil.getPage(offset, size);
        FactoryTestConf conf = new FactoryTestConf();
        conf.setConfName(confName);
        LambdaQueryWrapper<FactoryTestConf> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Objects.nonNull(conf.getId()), FactoryTestConf::getId, conf.getId()).like(Objects.nonNull(conf.getConfName()), FactoryTestConf::getConfName, conf.getConfName())
                .eq(FactoryTestConf::getDelFlag, FactoryTestConf.DEL_NORMAL).orderByDesc(FactoryTestConf::getCreateTime);
        return R.ok(factoryTestConfMapper.selectPage(page, wrapper));
    }
    
    @Override
    public R reomveOne(Long id) {
        if (Objects.isNull(id)) {
            return R.fail("id不能为空");
        }
        FactoryTestConf updater = new FactoryTestConf();
        updater.setDelFlag(FactoryTestConf.DEL_DEL);
        updater.setId(id);
        LambdaUpdateWrapper<FactoryTestConf> updateWrapper = Wrappers.lambdaUpdate();
        
        updateWrapper.eq(Objects.nonNull(id), FactoryTestConf::getId, updater.getId());
        
        factoryTestConfMapper.update(updater, updateWrapper);
        return R.ok();
    }
    
    @Override
    public R saveOne(FactoryTestConf conf) {
        if (Objects.isNull(conf.getConfName())) {
            return R.fail("配置名称不能为空");
        }
        if (Objects.isNull(conf.getJsonContent())) {
            return R.fail("配置内容不能为空");
        }
        conf.setCreateTime(System.currentTimeMillis());
        conf.setUpdateTime(System.currentTimeMillis());
        int row = baseMapper.insert(conf);
        if (row != 0) {
            return R.ok();
        }
        return R.fail("数据异常");
    }
    
    @Override
    public R updateOne(FactoryTestConf updater) {
        
        if (Objects.isNull(updater.getId())) {
            return R.fail("id不能为空");
        }
        if (Objects.isNull(updater.getConfName())) {
            return R.fail("配置名称不能为空");
        }
        if (Objects.isNull(updater.getJsonContent())) {
            return R.fail("配置内容不能为空");
        }
        updater.setUpdateTime(System.currentTimeMillis());
        boolean result = updateById(updater);
        if (result) {
            return R.ok();
        }
        return R.fail("更新失败");
    }
    
    @Override
    public R updateOneShelf(Long id, Integer shelfStatus) {
        if (Objects.isNull(id)) {
            return R.fail("id不能为空");
        }
        
        FactoryTestConf updater = new FactoryTestConf();
        updater.setUpdateTime(System.currentTimeMillis());
        updater.setId(id);
        updater.setShelfStatus(shelfStatus);
        int result = factoryTestConfMapper.updateOneShelf(updater);
        if (result > 0) {
            return R.ok();
        }
        return R.fail("上下架失败");
    }
    
    @Override
    public R detailById(Long id) {
        if (Objects.isNull(id)) {
            return R.fail("id不能为空");
        }
        FactoryTestConf conf = getById(id);
        if (Objects.isNull(conf)) {
            return R.fail("记录不存在");
        }
        return R.ok(conf);
    }
}
