package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.FactoryTestConf;

import java.util.List;

/**
 * @Author lny
 * @date 2023/8/18 14:17
 * @Description:
 **/
public interface FactoryTestConfMapper extends BaseMapper<FactoryTestConf> {
    
    /**
     * 查询所有
     *
     * @return
     */
    List<FactoryTestConf> queryAll(FactoryTestConf condition);
    
    int updateOneShelf(FactoryTestConf updater);
}
