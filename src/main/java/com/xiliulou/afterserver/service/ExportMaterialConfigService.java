package com.xiliulou.afterserver.service;

import com.xiliulou.afterserver.entity.ExportMaterialConfig;

import java.util.List;

/**
 * 物料导出配置表(ExportMaterialConfig)表服务接口
 *
 * @author zhangbozhi
 * @since 2024-08-30 11:21:07
 */
public interface ExportMaterialConfigService {
    
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    ExportMaterialConfig queryById(Integer id);
    
    /**
     * 分页查询
     *
     * @param exportMaterialConfig 筛选条件
     * @return 查询结果
     */
    List<ExportMaterialConfig> listByLimit(ExportMaterialConfig exportMaterialConfig, Long offset, Long size);
    
    /**
     * 分页count
     *
     * @param exportMaterialConfig 筛选条件
     * @return 查询结果
     */
    Long count(ExportMaterialConfig exportMaterialConfig);
    
    /**
     * 新增数据
     *
     * @param exportMaterialConfig 实例对象
     * @return 实例对象
     */
    Integer insert(List<ExportMaterialConfig> exportMaterialConfig);
    
    /**
     * 修改数据
     *
     * @param exportMaterialConfig 实例对象
     * @return 实例对象
     */
    Integer update(List<ExportMaterialConfig> exportMaterialConfigs);
    
    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);
    
    /**
     * 通过主键逻辑删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean removeById(Integer id);
    
}
