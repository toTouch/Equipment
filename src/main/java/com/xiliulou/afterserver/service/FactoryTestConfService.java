package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.FactoryTestConf;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ApiRequestQuery;

import java.util.List;

/**
 * @Author lny
 * @date 2023/8/18 14:22
 * @Description: 工厂测试配置
 **/
public interface FactoryTestConfService extends IService<FactoryTestConf> {
    
    /**
     * 查询所有
     *
     * @param apiRequestQuery
     * @return
     */
    List<FactoryTestConf> getAllByApi(ApiRequestQuery apiRequestQuery);
    
    
    R<IPage<FactoryTestConf>> getPage(Long offset, Long size, String confName);
    
    R reomveOne(Long id);
    
    R saveOne(FactoryTestConf conf);
    
    R updateOne(FactoryTestConf updater);
    
    R detailById(Long id);
    
    R updateOneShelf(Long ids, Integer shelfStatus, List<Long> supplierIds);
}
