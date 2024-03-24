package com.xiliulou.afterserver.service;

import com.xiliulou.afterserver.entity.MaterialCoreConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

/**
 * 物料核心配置(MaterialCoreConfig)表服务接口
 *
 * @author makejava
 * @since 2024-03-21 19:03:03
 */
public interface MaterialCoreConfigService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    MaterialCoreConfig queryById(Integer id);

    /**
     * 分页查询
     *
     * @param materialCoreConfig 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    Page<MaterialCoreConfig> queryByPage(MaterialCoreConfig materialCoreConfig, PageRequest pageRequest);

    /**
     * 新增数据
     *
     * @param materialCoreConfig 实例对象
     * @return 实例对象
     */
    MaterialCoreConfig insert(MaterialCoreConfig materialCoreConfig);

    /**
     * 修改数据
     *
     * @param materialCoreConfig 实例对象
     * @return 实例对象
     */
    int update(MaterialCoreConfig materialCoreConfig);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);
}
