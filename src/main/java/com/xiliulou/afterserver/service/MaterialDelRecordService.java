package com.xiliulou.afterserver.service;

import com.xiliulou.afterserver.entity.MaterialDelRecord;

import java.util.List;

/**
 * 物料删除追溯表(MaterialDelRecord)表服务接口
 *
 * @author zhangbozhi
 * @since 2024-06-27 20:44:34
 */
public interface MaterialDelRecordService {
    
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    MaterialDelRecord queryById(Long id);
    
    /**
     * 分页查询
     *
     * @param materialDelRecord 筛选条件
     * @return 查询结果
     */
    List<MaterialDelRecord> listByLimit(MaterialDelRecord materialDelRecord, Long offset, Long size);
    
    /**
     * 分页count
     *
     * @param materialDelRecord 筛选条件
     * @return 查询结果
     */
    Long count(MaterialDelRecord materialDelRecord);
    
    /**
     * 新增数据
     *
     * @param materialDelRecord 实例对象
     * @return 实例对象
     */
    MaterialDelRecord insert(MaterialDelRecord materialDelRecord);
    
    /**
     * 修改数据
     *
     * @param materialDelRecord 实例对象
     * @return 实例对象
     */
    MaterialDelRecord update(MaterialDelRecord materialDelRecord);
    
    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Long id);
    
    /**
     * 通过主键逻辑删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean removeById(Long id);
    
}
