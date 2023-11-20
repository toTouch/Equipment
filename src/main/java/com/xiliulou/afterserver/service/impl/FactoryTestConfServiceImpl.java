package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.xiliulou.afterserver.constant.CommonConstants;
import com.xiliulou.afterserver.entity.FactoryTestConf;
import com.xiliulou.afterserver.entity.ProductNew;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.mapper.FactoryTestConfMapper;
import com.xiliulou.afterserver.mapper.ProductNewMapper;
import com.xiliulou.afterserver.service.FactoryTestConfService;
import com.xiliulou.afterserver.service.SupplierService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ApiRequestQuery;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author lny
 * @date 2023/8/18 14:26
 * @Description:
 **/
@Slf4j
@Service
public class FactoryTestConfServiceImpl extends ServiceImpl<FactoryTestConfMapper, FactoryTestConf> implements FactoryTestConfService {
    
    @Autowired
    ProductNewMapper productNewMapper;
    
    @Resource
    private FactoryTestConfMapper factoryTestConfMapper;
    
    @Autowired
    private SupplierService supplierService;
    
    
    @Override
    public List<FactoryTestConf> getAllByApi(ApiRequestQuery apiRequestQuery) {
        FactoryTestConf condition = new FactoryTestConf();
        if (Objects.isNull(apiRequestQuery) && ObjectUtils.isEmpty(apiRequestQuery.getData())) {
            return new ArrayList<>();
        }
        
        CabinetFactoryTestInfo factoryTestInfo = JSON.parseObject(apiRequestQuery.getData(), CabinetFactoryTestInfo.class);
        List<ProductNew> productNews;
        List<Long> supplierIds = new ArrayList<>();
        
        if (Objects.nonNull(factoryTestInfo) && CollectionUtils.isNotEmpty(factoryTestInfo.getNoList())) {
            productNews = productNewMapper.selectListByNoList(factoryTestInfo.getNoList());
            if (Objects.nonNull(productNews)) {
                supplierIds = productNews.stream().map(ProductNew::getSupplierId).collect(Collectors.toList());
            }
        }
        
        condition.setSupplierIdList(supplierIds);
        
        List<FactoryTestConf> factoryTestConfs = factoryTestConfMapper.queryAll(condition);
        if (CollectionUtils.isEmpty(factoryTestConfs) || CollectionUtils.isEmpty(supplierIds)) {
            return factoryTestConfs;
        }
        
        // 去重
        HashSet<Long> supplierIdsSet = new HashSet<>(supplierIds);
        factoryTestConfs = factoryTestConfs.stream().filter(item -> containsAnySupplierIds(item.getSupplierIds(), supplierIdsSet)).collect(Collectors.toList());
        
        return factoryTestConfs;
    }
    
    private boolean containsAnySupplierIds(String supplierIds, HashSet<Long> values) {
        if (CollectionUtils.isEmpty(values) || StringUtils.isBlank(supplierIds)) {
            return false;
        }
        
        for (Long value : values) {
            if (supplierIds.contains(value.toString())) {
                return true;
            }
        }
        return false;
    }
    
    
    @Override
    public R getPage(Long offset, Long size, String confName) {
        Page page = PageUtil.getPage(offset, size);
        FactoryTestConf conf = new FactoryTestConf();
        conf.setConfName(confName);
        LambdaQueryWrapper<FactoryTestConf> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Objects.nonNull(conf.getId()), FactoryTestConf::getId, conf.getId()).like(Objects.nonNull(conf.getConfName()), FactoryTestConf::getConfName, conf.getConfName())
                .eq(FactoryTestConf::getDelFlag, FactoryTestConf.DEL_NORMAL).orderByDesc(FactoryTestConf::getCreateTime);
        
        Page<FactoryTestConf> factoryTestConfList = factoryTestConfMapper.selectPage(page, wrapper);
        List<FactoryTestConf> factoryTestConfListRecords = factoryTestConfList.getRecords();
        
        Map<Long, String> supplierNameFromIdMap = getSupplierNameFromIdMap(factoryTestConfListRecords);
        if (CollectionUtil.isEmpty(supplierNameFromIdMap)) {
            return R.ok(factoryTestConfList);
        }
        
        factoryTestConfListRecords.forEach(item -> {
            if (StringUtils.isBlank(item.getSupplierIds())) {
                return;
            }
            
            List<Long> supplierIds = JSON.parseArray(item.getSupplierIds(), Long.class);
            List<String> supplierNames = new ArrayList<>();
            
            item.setSupplierIdList(supplierIds);
            fillInFactoryName(supplierNameFromIdMap, item, supplierNames);
            
            item.setSupplierNameList(supplierNames);
        });
        return R.ok(factoryTestConfList);
    }
    
    private static void fillInFactoryName(Map<Long, String> supplierNameFromIdMap, FactoryTestConf item, List<String> supplierNames) {
        item.getSupplierIdList().forEach(sid -> {
            supplierNames.add(supplierNameFromIdMap.getOrDefault(sid, StringUtils.EMPTY));
        });
    }
    
    private Map<Long, String> getSupplierNameFromIdMap(List<FactoryTestConf> factoryTestConfListRecords) {
        Set<Long> factoryTestConfSet = new HashSet<>();
        
        if (CollectionUtils.isEmpty(factoryTestConfListRecords)) {
            return Collections.emptyMap();
        }
        factoryTestConfListRecords.forEach(item -> {
            if (StringUtils.isNotBlank(item.getSupplierIds())) {
                List<Long> list = JSON.parseArray(item.getSupplierIds(), Long.class);
                factoryTestConfSet.addAll(list);
            }
        });
        
        if (CollectionUtils.isEmpty(factoryTestConfSet)) {
            return Collections.emptyMap();
        }
        List<Supplier> supplierList = supplierService.ListBySupplierIds(factoryTestConfSet);
        
        if (CollectionUtils.isEmpty(supplierList)) {
            return Collections.emptyMap();
        }
        return supplierList.stream().collect(Collectors.toMap(Supplier::getId, Supplier::getName, (k1, k2) -> k1));
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
    public R updateOneShelf(FactoryTestConf factoryTestConf) {
        Long id = factoryTestConf.getId();
        List<Long> supplierIdList = factoryTestConf.getSupplierIdList();
        Integer shelfStatus = factoryTestConf.getShelfStatus();
        
        if (Objects.isNull(id)) {
            return R.fail("id不能为空");
        }
        
        FactoryTestConf updater = new FactoryTestConf();
        updater.setUpdateTime(System.currentTimeMillis());
        updater.setId(id);
        updater.setShelfStatus(shelfStatus);
        
        if (Objects.nonNull(supplierIdList)){
            updater.setSupplierIds(JSON.toJSONString(supplierIdList));
            // 长度限制
            if (supplierIdList.size() > CommonConstants.FILL_MAX_NO) {
                return R.fail("工厂数不能超过50个");
            }
        }
        
        if (SqlHelper.retBool(factoryTestConfMapper.updateOneShelf(updater))) {
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

@Data
class CabinetFactoryTestInfo extends FactoryTestConf {
    
    /**
     * 压测柜机sn
     */
    private List<String> noList;
    
    private String iotCard;
}
